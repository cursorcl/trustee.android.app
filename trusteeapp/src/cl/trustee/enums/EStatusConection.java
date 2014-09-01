package cl.trustee.enums;

import cl.trustee.R;

public enum EStatusConection {

	NO_CONECTION(0, R.drawable.no_conexion), UNSAFE(1, R.drawable.no_segura), NO_VALITED(
			2, R.drawable.exclamacion), SAFE(3, R.drawable.segura), SAFE_NO_VERIFICATED(
			4, R.drawable.noverificado);

	private int id;
	private int resource;

	private EStatusConection(int id, int resource) {
		this.id = id;
		this.resource = resource;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getResource() {
		return resource;
	}

	public void setResource(int resource) {
		this.resource = resource;
	}

	public static EStatusConection getById(int id) {
		return EStatusConection.values()[id % 4];
	}
}
