import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;


class Pair{
     private List<Declaration> declarationList;
     private Type returnType;
     
     Pair(List<Declaration> declarationList, Type r)
     {
     	   this.declarationList= declarationList;
     	   this.returnType = r;
     }
     
     Type getReturnType()
     {
           return returnType;
     }
     
     List<Declaration> getDL()
     {
           return declarationList;
     }
     
}

public class Typechecker {

  	private List<Type> types = new ArrayList<>();
  	
  	private Map<String, Pair> funEnvironment = new HashMap<String, Pair>();
  	
  	
  	public Typechecker() 
  	{
	  	this.types.add(new Type("num"));
		this.types.add(new Type("bool"));
		this.types.add(new Type("void"));
	}

  	public void typecheckProgram(Program program) throws Exception {
    		this.typecheckFunDeclarationList(program.funDeclarationList);
		//System.out.println("-------------------------------------------------------------------");
		System.out.println("BLOCK \n"+program.block+" \nevaluates to "+ this.typecheckBlock(new HashMap<String, Type>() , program.block) );
		System.out.println("------------------------------------------------------------------------\n");
	}

	
	public void typecheckFunDeclarationList(List<FunctionDeclaration> funDeclarationList) throws Exception
	{
		for(FunctionDeclaration fd : funDeclarationList) 
		{	
      			this.funEnvironment.put(fd.funname, new Pair(fd.declarationList, fd.ret_type));
      			//System.out.println("fun dec list "+fd.funname);
      			Map<String, Type> typeEnvironment = new HashMap<String, Type>();
      			if(fd.declarationList != null)
      			{
      				for(Declaration d : fd.declarationList) 
      				{
      					typeEnvironment.put(d.varname, d.type);
      					//System.out.print("   "+d.varname);
				}
				//System.out.println();
      			}
			//System.out.println("-------------------------------------------------------------------");
			System.out.println( "Function \""+fd+"\" \nevaluates "+ this.typecheckBody(fd, typeEnvironment, fd.body) +"  ");
			System.out.println("------------------------------------------------------------------------\n");
		}
		
	}
	
	
	public Type typecheckBody(FunctionDeclaration fd, Map<String, Type> typeEnvironment, FunctionBody body) throws Exception
	{
		//System.out.println("Body");
		Type b = this.typecheckBlock(typeEnvironment, body.block) ;
		//System.out.println("b= "+b);
		if(b.getClass().equals(funEnvironment.get(fd.funname).getReturnType().getClass()) )
			return b;
		throw new Exception("Function \""+fd.funname+"\" didn't typecheck. Return types mismatch.");
	}
	
	
	public Type typecheckBlock(Map<String, Type> typeEnvironment, Block block) throws Exception
	{
		if(block.declarationList != null)
      		{
      			for(Declaration d : block.declarationList) 
      			{
      				typeEnvironment.put(d.varname, d.type);
			}
      		}
		
	        return this.typecheckSequence(typeEnvironment,block.seq) ;
	}
	
	
	public Type typecheckSequence(Map<String, Type> typeEnvironment, Sequence seq) throws Exception
	{
		
		for(Instruction i:seq.instr)
		{
			//System.out.println("type: "+i);
			if(i instanceof Return)
				return this.typecheckReturn(typeEnvironment,(Return)i);
			else if(i instanceof Loop)
				this.typecheckLoop(typeEnvironment, (Loop)i);
			else if(i instanceof IfCondition)
				this.typecheckIf(typeEnvironment, (IfCondition)i);
			else if(i instanceof Assign)
				this.typecheckAssign(typeEnvironment, (Assign)i);
			else if(i instanceof FunctionCall)
			{
				//System.out.println("funcall");
				this.typecheckFunccall(typeEnvironment, (FunctionCall)i); 
			}
				
		}
		return null;
	}
	
	public Type typecheckReturn(Map<String, Type> typeEnvironment,Return r) throws Exception
	{
		Type t= typecheckExpression(typeEnvironment,r.e);
		//System.out.println("-------------------------------------------------------------------");
		System.out.println("RETURN\n"+r+" \nevaluates to "+ t);
		System.out.println("------------------------------------------------------------------------\n");
		return t;
	}
	
	
	public void typecheckAssign(Map<String, Type> typeEnvironment, Assign assign) throws Exception
	{
		Type left = this.typecheckIdExpr(typeEnvironment,assign.left);
		Type right = this.typecheckExpression(typeEnvironment,assign.right);
		if(left.getClass().getName().equals(right.getClass().getName()))
		{
			//System.out.println("-------------------------------------------------------------------");
			System.out.println("ASSIGN\n"+assign+" \nevaluates to null ");
			System.out.println("------------------------------------------------------------------------\n");
		}
		else
			throw new Exception(assign+" didn't typecheck.");
	}


	public void typecheckIf(Map<String, Type> typeEnvironment, IfCondition cond) throws Exception
	{
		Type b=this.typecheckSubExpression(typeEnvironment,cond.pred);
		//System.out.println("if cond "+b);
		if(b instanceof BoolType)
		{
			Type t1 = this.typecheckSubExpression(typeEnvironment,cond.e1);
			Type t2 = this.typecheckSubExpression(typeEnvironment,cond.e2);
			
			if(t1 == null && t2 == null)
			{
				//System.out.println("-------------------------------------------------------------------");
				System.out.println("IF CONDITION\n"+cond+" \nevaluates to null");
				System.out.println("------------------------------------------------------------------------\n");
			}	
			
			else if(t1.getClass().getName().equals(t2.getClass().getName()))
			{
				//System.out.println("-------------------------------------------------------------------");
				System.out.println("IF CONDITION\n"+cond+" \nevaluates to " +t1);
				System.out.println("------------------------------------------------------------------------\n");
			}
			else
				throw new Exception(cond+" didn't typecheck.");

		}
		else
			throw new Exception(cond+" didn't typecheck.");
	}
	
	public Type typecheckSubExpression(Map<String, Type> typeEnvironment, Subexpr subexpr) throws Exception
	{
			
		if(subexpr.a != null)
		{
			this.typecheckAssign(typeEnvironment,subexpr.a);
			return null;
		}
		else if(subexpr.e != null)
		{
			return this.typecheckExpression(typeEnvironment,subexpr.e);	
		}
		else
			return null;
	}
	
	public void typecheckLoop(Map<String, Type> typeEnvironment, Loop loops) throws Exception
	{
		
		Type b=this.typecheckExpression(typeEnvironment,loops.pred);
		
		//for while blocks, i assumed, no returns inside loop
		//for that sake, block inside while ,type checking process of it, is done in this method , instead of calling blocktypecheck method.
		//i can also get rid of return type expression check , in sequence forloop you can see that, we never made comp with return
		//we throw exception if other than loop,if and assign occurs.
		if(b instanceof BoolType)
		{
			
			if(loops.block.declarationList != null)
      			{
      				for(Declaration d : loops.block.declarationList) 
      				{
      					typeEnvironment.put(d.varname, d.type);
				}
      			}
      			
      			
			for(Instruction i:loops.block.seq.instr)
			{
				if(i instanceof Loop)
					this.typecheckLoop(typeEnvironment, (Loop)i);
				else if(i instanceof IfCondition)
					this.typecheckIf(typeEnvironment, (IfCondition)i);
				else if(i instanceof Assign)
					this.typecheckAssign(typeEnvironment, (Assign)i); 
				else if(i instanceof FunctionCall)
					this.typecheckFunccall(typeEnvironment, (FunctionCall)i); 
				else
					throw new Exception("LOOP didn't type check. Presence of return statement");
				
			}
			//System.out.println("-------------------------------------------------------------------");
			System.out.println("LOOP\n"+loops+" \nevaluates to null.");	
			System.out.println("------------------------------------------------------------------------\n");
		
		}
		else
			throw new Exception("LOOP\n "+loops+" didn't type check. ");
	}
	
	
  	public Type typecheckExpression(Map<String, Type> typeEnvironment,Expr e) throws Exception {
    		
    		if(e instanceof AddExpr) {
      			return typecheckAddExpr(typeEnvironment,(AddExpr)e);
    		}
		else if(e instanceof MulExpr) {
		  	return typecheckMulExpr(typeEnvironment,(MulExpr)e);
		}
		else if(e instanceof NegExpr) {
      			return typecheckNegExpr(typeEnvironment,(NegExpr)e);
    		}
		else if(e instanceof NumExpr) {
      			return typecheckNumExpr(typeEnvironment,(NumExpr)e);
    		}
		else if(e instanceof IdExpr) {
      			return typecheckIdExpr(typeEnvironment,(IdExpr)e);
    		}
    		else if(e instanceof BoolExpr) {
      			return typecheckBoolExpr(typeEnvironment,(BoolExpr)e);
    		}
    		else if(e instanceof FunctionCall) {
      			return typecheckFunccall(typeEnvironment,(FunctionCall)e);
    		}
		else {
		  	return new VoidType();
		}
	}
	
	private Type typecheckFunccall(Map<String, Type> typeEnvironment, FunctionCall e) throws Exception{
		
		if(funEnvironment.containsKey(e.funname))
		{
			if(funEnvironment.get(e.funname).getDL().size() == e.declarationList.size())
			{
				List<Declaration> fp= funEnvironment.get(e.funname).getDL();
				int size= e.declarationList.size();
				for(int i=0;i<size;i++)
				{
					Type formal=   fp.get(i).type;
					Type actual=   this.typecheckExpression(typeEnvironment,e.declarationList.get(i));
					
					if(! formal.getClass().getName().equals(actual.getClass().getName()))
						throw new Exception("Function call \""+e+"\" didn't typecheck. Parameter type mismatch.");
				}
				
				//System.out.println("-------------------------------------------------------------------");
				System.out.println("FUNCTION CALL\n"+e+" \nevaluates to "+funEnvironment.get(e.funname).getReturnType());
				System.out.println("------------------------------------------------------------------------\n");
				
				return funEnvironment.get(e.funname).getReturnType();
			}
			else throw new Exception("Function call \""+e+"\" didn't typecheck.Number of arguments mismatch.");
			
		}
		throw new Exception(e.funname+" Not defined.");
	}
	

	private Type typecheckAddExpr(Map<String, Type> typeEnvironment, AddExpr e) throws Exception {
	  	Type leftType = this.typecheckExpression(typeEnvironment,e.left);
		Type rightType = this.typecheckExpression(typeEnvironment,e.right);
	  	if(leftType instanceof NumType && rightType instanceof NumType) {
		  	//System.out.println("-------------------------------------------------------------------");
			System.out.println("EXPRESSION\n"+e+" \nevaluates to "+leftType);
			System.out.println("------------------------------------------------------------------------\n");
		  	return leftType;
		}
		throw new Exception("AddExpr: didn't typecheck.");
	}

	private Type typecheckMulExpr(Map<String, Type> typeEnvironment, MulExpr e) throws Exception {
	  	Type leftType = this.typecheckExpression(typeEnvironment,e.left);
		Type rightType = this.typecheckExpression(typeEnvironment,e.right);
	  	if(leftType instanceof NumType && rightType instanceof NumType) {
		  	//System.out.println("-------------------------------------------------------------------");
			System.out.println("EXPRESSION\n"+e+" \nevaluates to "+leftType);
			System.out.println("------------------------------------------------------------------------\n");
		  	
		  	return leftType;
		}
		throw new Exception("MulExpr: didn't typecheck.");
	}

	private Type typecheckNegExpr(Map<String, Type> typeEnvironment, NegExpr e) throws Exception {
	  	Type type = this.typecheckExpression(typeEnvironment,e.expr);
	  	if(type instanceof NumType) {
			  //System.out.println("-------------------------------------------------------------------");
			  System.out.println("EXPRESSION\n"+e+" \nevaluates to "+type);
			  System.out.println("------------------------------------------------------------------------\n");
			  return type;
		}
		throw new Exception("MulExpr: didn't typecheck.");
	}

	private Type typecheckNumExpr(Map<String, Type> typeEnvironment, NumExpr e) throws Exception {
    		return new NumType();
  	}
  	
  	private Type typecheckBoolExpr(Map<String, Type> typeEnvironment, BoolExpr e) throws Exception {
    		return new BoolType();
  	}

	private Type typecheckIdExpr(Map<String, Type> typeEnvironment,IdExpr e) throws Exception {
	  	
	  	if(!(typeEnvironment.containsKey(e.name))) 
	  	{
      			throw new Exception("Variable " + e.name + " not declared.");
		}
	  	return typeEnvironment.get(e.name);
  	}
}
