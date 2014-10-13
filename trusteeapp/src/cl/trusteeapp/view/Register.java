package cl.trusteeapp.view;

import java.io.Serializable;

import cl.trusteeapp.enums.EStatusConection;

public class Register implements Serializable {
	private static final long serialVersionUID = 1L;
	private String number;
	private String description;
	private String image;
	private EStatusConection status;
	private int clientePago = 0;
	private long date;
	
	public Register() {
		number = "";
		status = EStatusConection.NO_CONECTION;
		description = "";
		image = null;
		date = System.currentTimeMillis();
	}

	public Register(String number, String description, EStatusConection status, long date, String image) {
		super();
		this.number = number;
		this.description = description;
		this.status = status;
		this.date = date;
		this.image = image;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public EStatusConection getStatus() {
		return status;
	}

	public void setStatus(EStatusConection status) {
		this.status = status;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public int getClientePago() {
		return clientePago;
	}

	public void setClientePago(int clientePago) {
		this.clientePago = clientePago;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	
}
