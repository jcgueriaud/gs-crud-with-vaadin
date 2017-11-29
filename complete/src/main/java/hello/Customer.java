package hello;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Customer {

	@Id
	@GeneratedValue
	private Long id;

	private String firstName;

	private String lastName;

	protected Customer() {
	}

	public Customer(String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public Long getId() {
		return id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Override
	public String toString() {
		return String.format("Customer[id=%d, firstName='%s', lastName='%s']", id,
				firstName, lastName);
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof Customer) {
			Customer customer = (Customer) object;
			if ((id == null)&&(customer.getId() == null)){ return super.equals(object);}
			return this.id.equals(customer.getId());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		if (this.id == null) return super.hashCode();
		return Long.hashCode(this.id);
	}
}
