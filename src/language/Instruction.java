package language;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import language.symbols.StringParser;

public class Instruction {
	
	private static List<SpecialRegister> specialRegisterBit = parseSFR("C:\\Users\\Juan\\Desktop\\Lenguajes\\Asm-SR.txt",0);
	private static List<SpecialRegister> specialRegisterByte = parseSFR("C:\\Users\\Juan\\Desktop\\Lenguajes\\Asm-SR.txt",1);
	//Necessary*************
	public String instr;
	public String code;
	public int bytes;
	public String instrName; // to have the name of the instruction example: MOV A,#01 instrName = MOV ,instr MOV A,##
	//*************************************************************************
	
	public Instruction(){
		this.instr="";
		this.code="";
		this.bytes=0;
	}

	public Instruction(String instr, String code, int bytes) {
		this.instr=instr;
		this.code=code;
		this.bytes=bytes;
		// initialize the values with the default that is 555

		int i=0;
		this.instrName="";
		while(instr.charAt(i)!=' '){
			this.instrName+=instr.charAt(i);
			i++;
		}
	}
	
	private static List<SpecialRegister> parseSFR(String ruta, int opc) { //opc decide si va a filtrar los bits o bytes. bits = 0, bytes = 1.
		ArrayList<SpecialRegister> array = new ArrayList<>();
		try(Scanner in = new Scanner(new File(ruta))){
			while(in.hasNextLine()) {
				String []ics = in.nextLine().split("-");
				if(opc == 0 && ics[2].equals("0")) {
					array.add(new SpecialRegister(ics[0],ics[1],ics[2]));
				}
				else if(opc == 1 && ics[2].equals("1")) {
					array.add(new SpecialRegister(ics[0],ics[1],ics[2]));
				}
			}
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return array;
	}
	
	//*********************
	// "&" REL
	// "$" BIT
	// "%" DIR
	// "#+" DATOS
	/**
	 * @param tokens: instrucci�n filtrada en tokens
	 * @return si lo que espera la instrucci�n tentativamente corresponde a la instrucci�n tokenizada
	 * */
	public LineInstruction isThisInstruction(String []tokens, String instruction, int numLine, int address){
		//"ADD A,#34H"
		ArrayList<String> transiciones = new ArrayList<String>();
		boolean flag = true;
		LineInstruction li = new LineInstruction(this,instruction,numLine,false,address);
				
		transiciones.addAll(Arrays.asList(StringParser.getTokens(this.instr))); //Transiciones esperadas (operandos)
		if( tokens.length != transiciones.size() )
			return null;
		
		for(int i=0;i<transiciones.size();i++) {
			if( transiciones.get(i).equals("&") || transiciones.get(i).equals("$") || transiciones.get(i).equals("%") || transiciones.get(i).equals("#+") ) {
				li.getDefinitions().add(transiciones.get(i));
				li.getProvided().add(tokens[i]);
				li.setNeedsResolution(true);
				flag &= li.getInstruction().canSolveSymbols(li);
			}
			else {
				flag &= tokens[i].equals(transiciones.get(i));
			}
			
			if( !flag )
				return null;
		}
		
		solveFixedInstruction(li);
		return li;
	}
	
	public String toString(){
		return String.format("[Name: %s Code: %s Number of bytes: %s]", this.instrName,this.code,this.bytes);
	}
	
	public boolean canSolveSymbols(LineInstruction is) {
		boolean killme = true;
		for( int i=0; i<is.getDefinitions().size(); i++ ) {
			if( is.getDefinitions().get(i).equals("&") ) {
				killme &= this.isRelative(is.getProvided().get(i));
			}
			else if( is.getDefinitions().get(i).equals("$") ) {
				killme &= this.isBit(is.getProvided().get(i));
			}
			else if( is.getDefinitions().get(i).equals("#+") ) {
				killme &= this.isDirect(is.getProvided().get(i));
			}
			else if( is.getDefinitions().get(i).equals("%") ) {
				killme &= this.isInmediate(is.getProvided().get(i));
			}
		}
		
		return killme;
	}
	
	private void solveFixedInstruction(LineInstruction li) {
		String hex = li.getInstruction().code;
		if( !li.isNeedsResolution() )
			li.setHex(hex);
	}
	
	public void solveInstruction(LineInstruction li, List<LineInstruction> others) {
		String theHex=""+code;
		if( bytes > 1 ) {//significa que hay que ir metiendo los operandos resueltos
			for( int i = 0; i < li.getProvided().size(); i++ ) {
				if( li.getDefinitions().get(i).equals("&") ) {
					theHex += solveRelative(li.getProvided().get(i),others);
				}
				else if( li.getDefinitions().get(i).equals("$") ) {
					theHex += solveBit(li.getProvided().get(i));
				}
				else if( li.getDefinitions().get(i).equals("#+") ) {
					theHex += solveDirect(li.getProvided().get(i));
				}
				else if( li.getDefinitions().get(i).equals("%") ) {
					theHex += solveInmediate(li.getProvided().get(0));
				}
				else { //Para direccionamientos por registro, indirectos y otros como A, C, etc.
					
				}
			}
		}
		
		li.setNeedsResolution(false);
		//poner aqu� lo del formato hex-80
		li.setHex80(theHex);
	}
	
	// REL &
	public boolean isRelative(String s){
		// example: JNZ ETIQUETA1
		return s.matches("^[a-zA-Z0-9_-]");
	}
	
	public String solveRelative(String provided, List<LineInstruction> others) {
		return null;
	}
	
	public boolean isBit(String provided){		
		for(SpecialRegister sr : specialRegisterBit) {
			if( provided.matches(String.format("[%s]{1}.[.0-7]?",sr.symbol)) ) {
				return true;
			}
		}
		
		return false;
	}
	
	public String solveBit(String provided) {
		return null;
	}
	
	public boolean isDirect(String str) {
		return str.matches("[#0-9]+?.[hbd]");
	}
	
	public String solveDirect(String provided) {
		int entero = 0;
		provided = provided.substring(1);
		if( provided.matches(".^[hbd]") ) {
			entero = Integer.parseInt(provided, 16);
		}
		else if( provided.endsWith("h") ) {
			entero = Integer.parseInt(provided.substring(0, provided.indexOf("h")), 16);
		}
		else if( provided.endsWith("b") ) {
			entero = Integer.parseInt(provided.substring(0, provided.indexOf("b")), 2);
		}
		else if( provided.endsWith("d") ) {
			entero = Integer.parseInt(provided.substring(0, provided.indexOf("d")), 10);
		}
		
		return Integer.toHexString(entero);
	}
	
	public boolean isInmediate(String str) {
		List<SpecialRegister> allRegisters = new ArrayList<>();
		allRegisters.addAll(specialRegisterBit);
		allRegisters.addAll(specialRegisterByte);
		
		for( SpecialRegister sr : allRegisters ) {
			if( sr.symbol.equals(str) )
				return true;
		}
		
		return str.matches("[[0-9]+?].[hbd]");
	}
	
	public String solveInmediate(String provided) {
		int entero = 0;
		if( provided.matches(".^[hbd]") ) {
			entero = Integer.parseInt(provided, 16);
		}
		else if( provided.endsWith("h") ) {
			entero = Integer.parseInt(provided.substring(0, provided.indexOf("h")), 16);
		}
		else if( provided.endsWith("b") ) {
			entero = Integer.parseInt(provided.substring(0, provided.indexOf("b")), 2);
		}
		else if( provided.endsWith("d") ) {
			entero = Integer.parseInt(provided.substring(0, provided.indexOf("d")), 10);
		}
		
		return Integer.toHexString(entero);
	}

}
