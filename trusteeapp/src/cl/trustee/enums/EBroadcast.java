package cl.trustee.enums;

public enum EBroadcast {
	END, ACCEPT, CANCEL;

	@Override
	public String toString() {
		return this.name();
	}

}
