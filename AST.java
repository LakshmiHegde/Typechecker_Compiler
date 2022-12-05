import java.util.*;

class Type 
{

  	public final String name;  	
	public Type(String name) 
	{
    		this.name = name;
	}

  	public boolean equals(Object type) 	
  	{
    		if(!(type instanceof Type)) 
    		{
		  	return false;
		}
		return this.name.equals(((Type)type).name);
	}

	public String toString() 
	{
	  	return this.name;
	}
}

class NumType extends Type {
  	public NumType() 
  	{
    		super("Num");
	}
}

class BoolType extends Type {
  	public BoolType() 
  	{
    		super("Bool");
	}
}

class VoidType extends Type {
  	public VoidType() 
  	{
		super("Void");
	}
}

class Declaration {
  
  	public final String varname;
	public final Type type;

	public Declaration(String varname, Type type) 
	{
    		this.varname = varname;
		this.type = type;
	}

	public String toString() 
	{
		//System.out.println("Dec");
    		return this.type.toString() + " " + this.varname;
	}
}


class Block{
	public final List<Declaration> declarationList;
	public final Sequence seq;
	
	
	public Block(List<Declaration> declarationList, Sequence seq){
		this.declarationList = declarationList;
		this.seq=seq;
	}
	public String toString() 
	{
    		String s = "";
    		//System.out.println("Block");
    		if(declarationList == null)
    			return seq.toString();
		for(Declaration d : this.declarationList) 
		{
      			s += d.toString() + "  ";
		}
		
		return s +  seq.toString() ;
	}

}


class FunctionDeclaration{
	public final Type ret_type;
	public final String funname;
	public final List<Declaration> declarationList;
	public final FunctionBody body;
	
	public FunctionDeclaration(Type type,String funname,List<Declaration> l,FunctionBody b){
		this.ret_type=type;
		this.funname=funname;
		this.declarationList=l;
		this.body=b;
	}
	
	public String toString(){
		String s = "";
		
		for(Declaration d : this.declarationList) 
		{
      			s += d.toString() + "   ";
		}
		return ret_type.toString()+"  "+funname+" ( "+s +" ) \n{ \n"+ this.body.toString()+"}";
	}
	

}


class FunctionBody{
	public final Block block;
	FunctionBody(Block b)
	{
		this.block=b;
	}
	public String toString(){
		
		return block.toString();
	}
}


class Program {
  	
  	public final List<FunctionDeclaration> funDeclarationList;
	public final Block block;

	public Program(List<FunctionDeclaration> funDeclarationList, Block block) 
	{
    		this.funDeclarationList = funDeclarationList;
		this.block=block;
	}

	public String toString() 
	{
    		String s = "";
    		//System.out.println("Program");
		for(FunctionDeclaration d : this.funDeclarationList) 
		{
      			s += d.toString() + "\n";
		}
		return s + this.block.toString();
	}

}





class Sequence{
	public List<Instruction> instr;
	
	public Sequence(List<Instruction> instr){
		this.instr=instr;
	}
	
	public Sequence(){
		this.instr = new ArrayList<>();
	}
	public String toString() 
	{
    		String s = "";
    		//System.out.println("Seq");
		for(Instruction i:instr) 
		{
      			s += i.toString() + "\n";
		}
		return s ;
	}

}

/*class Instruction {

	public final Assign a;
	public final Return r;
	
	public Instruction(Assign a)
	{
		this.a=a;
		this.r=null;
	}
	
	public Instruction(Return r)
	{
		this.r=r;
		this.a=null;
	}
	public String toString(){
		//System.out.println("Instr");
		return a != null? a.toString():"" + r!=null? r.toString():"";
	}
}*/

interface Instruction{};


class Assign implements Instruction {
  	public final Expr right;
  	public final IdExpr left;
	public Assign(IdExpr left, Expr right) 
	{
	  this.left=left;
	  this.right=right;
  	}
	
	
	public String toString() { return "\n"+this.left+" = "+this.right ; }
}

class Return implements Instruction {
  	public final Expr e;
	public Return(Expr e) 
	{
	  this.e=e;
  	}
	
	public String toString() { return "\nreturn "+this.e ; }
}


class Loop implements Instruction {
  	public final Expr pred;
  	public final Block block;
	public Loop(Expr e, Block block) 
	{
	  this.pred=e;
	  this.block=block;
  	}
  	
  	
	public String toString() { return "LOOP ( "+this.pred +" )\n{\n" +this.block +"\n}" ; }
}

class IfCondition implements Instruction {
  	public final Subexpr e1, e2, pred;
	public IfCondition(Subexpr e,Subexpr e1, Subexpr e2) 
	{
	  	this.pred=e;
	  	this.e1=e1;
	  	this.e2=e2;
  	}
  	
  	
	public String toString() { return "IF ( "+this.pred +" ) then " +this.e1 +" else "+this.e2 ; }
}


class Subexpr{
	
	public final Assign a;
	public final Expr e;
	
	
	public Subexpr(Assign a)
	{
		this.a=a;
		this.e=null;
	}
	
	public Subexpr(Expr e)
	{
		this.e=e;
		this.a=null;
	}
	public String toString(){
		
		return  a!=null? a.toString():"" + e!=null? e.toString() : "";
	} 

}

interface Expr extends Instruction{
  public abstract int evaluate();
}

class AddExpr implements Expr 
{
  	public final Expr left;
	public final Expr right;

  	public AddExpr(Expr left, Expr right) 
  	{
    		this.left = left;
		this.right = right;
	}

	public int evaluate() { return this.left.evaluate() + this.right.evaluate(); }
	public String toString() { return this.left.toString() + " + " + this.right.toString(); }
}

class MulExpr implements Expr {
  	public final Expr left;
	public final Expr right;

  	public MulExpr(Expr left, Expr right) {
  	  	this.left = left;
		this.right = right;
	}

	public int evaluate() { return this.left.evaluate() *+ this.right.evaluate(); }
	public String toString() { return this.left.toString() + " * " + this.right.toString(); }
}

class NegExpr implements Expr {
  	Expr expr;
	
	public NegExpr(Expr expr) 
	{
	  this.expr = expr;
  	}

	public int evaluate() { return this.expr.evaluate(); }
	public String toString() { return "-(" + this.expr.toString() + ")"; }
}

class NumExpr implements Expr {
  	public final Integer value;
	
	public NumExpr(Integer value) 
	{
	  this.value = value;
  	}

	public int evaluate() { return this.value; }
	public String toString() { return this.value.toString(); }
  
}

class BoolExpr implements Expr {
  	public final Boolean value;
	
	public BoolExpr(Boolean value) 
	{
	  this.value = value;
  	}

	public int evaluate() { return 0; }
	public String toString() { return this.value.toString(); }
  
}

class IdExpr implements Expr {
  	public final String name;
	public IdExpr(String name) 
	{
	  this.name = name;
  	}
	
	public int evaluate() { return 0; }
	public String toString() { return this.name; }
}




class FunctionCall implements Expr{
	
	public final String funname;
	public final List<Expr> declarationList;
	
	
	public FunctionCall(String id,List<Expr> l){
		this.funname=id;
		this.declarationList=l;
	}
	
	public int evaluate() { return 0; }
	
	public String toString(){
		String s = "";
		for(Expr d : this.declarationList) 
		{
      			s += d.toString() + "   ";
		}
		return funname+" ( "+s +") ";
	}
	
}

