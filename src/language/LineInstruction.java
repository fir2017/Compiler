package language;

import java.util.ArrayList;
import java.util.List;

public class LineInstruction {
	private String hex80;
	private String hex;
	private Instruction instruction;
	private String linea;
	private int numLinea;
	private boolean needsResolution;
	private List<String> definition;
	private List<String> provided;
	private int address;
	private String tag;
	
	public LineInstruction(Instruction instruction, String linea, int numLinea, boolean nR, int address) {
		super();
		this.instruction = instruction;
		this.linea = linea;
		this.numLinea = numLinea;
		this.needsResolution = nR;
		this.definition = new ArrayList<String>();
		this.provided = new ArrayList<String>();
		this.hex80="";
		this.hex="";
		this.address=address;
		this.tag = "";
	}
	
	@Override public String toString() {
		return String.format("[Hex:%s, Instruction:%s,Address:%d,NumLine:%d,Tag:%s]",hex,instruction,address,numLinea,tag);
	}
	
	public void setTag(String str) {
		tag = str;
	}
	
	public String getTag() {
		return tag;
	}
	
	public int getAddress() {
		return address;
	}
	
	public void setHex80(String hex80) {
		this.hex80 = hex80;
	}
	
	public void setHex(String hex) {
		this.hex = hex;
	}
	
	public String getHex() {
		return this.hex;
	}
	
	public String getHex80() {
		return this.hex80;
	}
	
	/**
	 * 
	 * @return Returns the list of definitions to be solved
	 * */
	public List<String> getDefinitions(){
		return definition;
	}
	
	public List<String> getProvided(){
		return provided;
	}
	
	public Instruction getInstruction() {
		return instruction;
	}
	public void setInstruction(Instruction instruction) {
		this.instruction = instruction;
	}
	public String getLinea() {
		return linea;
	}
	public void setLinea(String linea) {
		this.linea = linea;
	}
	public int getNumLinea() {
		return numLinea;
	}
	public void setNumLinea(int numLinea) {
		this.numLinea = numLinea;
	}
	public boolean isNeedsResolution() {
		return needsResolution;
	}
	public void setNeedsResolution(boolean needsResolution) {
		this.needsResolution = needsResolution;
	}
}
