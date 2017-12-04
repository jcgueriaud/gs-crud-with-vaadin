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
import org.vaadin.extension.gridscroll.GridScrollExtension;

import java.util.List;

@SpringUI
public class VaadinUI extends UI {

	private final CustomerRepository repo;

	private final CustomerEditor editor;

	final Grid<Customer> grid;

	final TextField filter;

	private final Button addNewBtn;
	final TextField scrollPositionY;
	private final Button scrollToPosition;
	private final Button savePosition;

	private final GridScrollExtension extendedGrid;
	@Autowired
	public VaadinUI(CustomerRepository repo, CustomerEditor editor) {
		this.repo = repo;
		this.editor = editor;
		this.grid = new Grid<>(Customer.class);
		this.filter = new TextField();
		scrollPositionY = new TextField();
		scrollPositionY.setPlaceholder("Scroll Position Y");
		this.addNewBtn = new Button("New customer", VaadinIcons.PLUS);
		this.scrollToPosition = new Button("Scroll To position");
		this.savePosition = new Button("Save scroll position");
		extendedGrid = new GridScrollExtension(grid);
	}

	@Override
	protected void init(VaadinRequest request) {
		// build layout
		HorizontalLayout actions1 = new HorizontalLayout(savePosition,scrollPositionY, scrollToPosition);
		HorizontalLayout actions2 = new HorizontalLayout(filter, addNewBtn);
		VerticalLayout mainLayout = new VerticalLayout(actions1,actions2, grid, editor);
		setContent(mainLayout);

		grid.setHeight(300, Unit.PIXELS);
		grid.setColumns("id", "firstName", "lastName");

		filter.setPlaceholder("Filter by last name");

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



		scrollToPosition.addClickListener(event -> {
			if (scrollPositionY.getValue() != null) {
				try {
					Integer scrollPos = Integer.parseInt(scrollPositionY.getValue());
					extendedGrid.setScrollPosition(0,scrollPos);
				} catch (NumberFormatException nfe){

				}

			}
		});

		savePosition.addClickListener(event -> {
			scrollPositionY.setValue(extendedGrid.getLastYPosition()+"");
		});
	}

}
