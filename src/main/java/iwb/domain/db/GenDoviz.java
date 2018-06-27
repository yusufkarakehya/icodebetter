package iwb.domain.db;


public class GenDoviz {
	
	private String paraTip;
	private String workDt;
	private double dalis;
	private double dsatis;
	private double ealis;
	private double esatis;
	private short exchangeBankTip;
	
	public GenDoviz() {
		super();
	}

	public GenDoviz(String paraTip, String workDt, double dalis, double dsatis,
			double ealis, double esatis, short exchangeBankTip) {
		super();
		this.paraTip = paraTip;
		this.workDt = workDt;
		this.dalis = dalis;
		this.dsatis = dsatis;
		this.ealis = ealis;
		this.esatis = esatis;
		this.exchangeBankTip = exchangeBankTip;
	}

	public String getParaTip() {
		return paraTip;
	}

	public void setParaTip(String paraTip) {
		this.paraTip = paraTip;
	}

	public String getWorkDt() {
		return workDt;
	}

	public void setWorkDt(String workDt) {
		this.workDt = workDt;
	}

	public double getDalis() {
		return dalis;
	}

	public void setDalis(double dalis) {
		this.dalis = dalis;
	}

	public double getDsatis() {
		return dsatis;
	}

	public void setDsatis(double dsatis) {
		this.dsatis = dsatis;
	}

	public double getEalis() {
		return ealis;
	}

	public void setEalis(double ealis) {
		this.ealis = ealis;
	}

	public double getEsatis() {
		return esatis;
	}

	public void setEsatis(double esatis) {
		this.esatis = esatis;
	}

	public short getExchangeBankTip() {
		return exchangeBankTip;
	}

	public void setExchangeBankTip(short exchangeBankTip) {
		this.exchangeBankTip = exchangeBankTip;
	}
	
	
	
}
