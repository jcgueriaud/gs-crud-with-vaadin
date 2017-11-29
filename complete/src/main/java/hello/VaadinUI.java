package hello;

import com.vaadin.data.provider.CallbackDataProvider;
import com.vaadin.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@SpringUI
public class VaadinUI extends UI {

	private final CustomerRepository repo;

	private final CustomerEditor editor;

	final Grid<Customer> grid;

	final TextField filter;

	private final Button addNewBtn;
	final TextField itemToSelect;
	private final Button scrollToItemBtn;

	@Autowired
	public VaadinUI(CustomerRepository repo, CustomerEditor editor) {
		this.repo = repo;
		this.editor = editor;
		this.grid = new Grid<>(Customer.class);
		this.filter = new TextField();
		itemToSelect = new TextField();
		itemToSelect.setPlaceholder("Choose an ID");
		this.addNewBtn = new Button("New customer", VaadinIcons.PLUS);
		this.scrollToItemBtn = new Button("Scroll To Item");
	}

	@Override
	protected void init(VaadinRequest request) {
		// build layout
		HorizontalLayout actions1 = new HorizontalLayout(itemToSelect, scrollToItemBtn);
		HorizontalLayout actions2 = new HorizontalLayout(filter, addNewBtn);
		VerticalLayout mainLayout = new VerticalLayout(actions1,actions2, grid, editor);
		setContent(mainLayout);

		grid.setHeight(300, Unit.PIXELS);
		grid.setColumns("id", "firstName", "lastName");

		filter.setPlaceholder("Filter by last name");

		// Hook logic to components


		// Connect selected Customer to editor or hide if none is selected
		grid.asSingleSelect().addValueChangeListener(e -> {
			editor.editCustomer(e.getValue());
		});

		// Instantiate and edit new Customer the new button is clicked
		addNewBtn.addClickListener(e -> editor.editCustomer(new Customer("", "")));

		// Listen changes made by the editor, refresh data from backend
		editor.setChangeHandler(() -> {
			editor.setVisible(false);
			grid.getDataProvider().refreshAll();
		});

		DataProvider<Customer, String> dataProvider =  	new CallbackDataProvider<>(
				query -> {
					String filter = query.getFilter().orElse("%");
					final List<Customer> page =  repo.findByLastNameStartsWithIgnoreCase(filter,new PageRequest(query.getOffset()/query.getLimit(),query.getLimit()));
					return page.subList(query.getOffset() % query.getLimit(), page.size()).stream();
				},
				query -> {
					String filter = query.getFilter().orElse("%");
					return repo.countByLastNameStartsWithIgnoreCase(filter);
				}
		);;

		ConfigurableFilterDataProvider<Customer, Void, String> filterDataProvider = dataProvider.withConfigurableFilter();
		grid.setDataProvider(filterDataProvider );
		filter.addValueChangeListener(event -> {
			filterDataProvider.setFilter(event.getValue());
		});



		scrollToItemBtn.addClickListener(event -> {
			if (itemToSelect.getValue() != null) {
				try {
					Long id = Long.parseLong(itemToSelect.getValue());
					Customer customer = repo.findById(id);
					if (customer != null)
						grid.select(customer); // how to scroll to the selected line ?
				} catch (NumberFormatException nfe){

				}

			}
		});
	}

}
