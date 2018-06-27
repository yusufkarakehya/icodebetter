package iwb.enums;

public enum FrameworkDataTypes {
	UNKNOWN(null),
	TEXT("string"),
	DATE("date"),
	FLOAT("double"),
	INTEGER("int"),
	BOOLEAN("int"),	AUTO("auto"),BIGDECIMAL("bigdecimal"),JSON("json"),
	LONG("long");
	
	static public FrameworkDataTypes fromInt(int tip){
		for (FrameworkDataTypes t:FrameworkDataTypes.values())
			if (t.ordinal()==tip)
				return t;
		return FrameworkDataTypes.UNKNOWN;		
	}
	
	private String wsname;
	
	public String getWsName(){
		return wsname;
	}
	
	private FrameworkDataTypes(String wsname) {
		this.wsname=wsname;
	}
}
