import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class Driver {

	public static void main(String[] args) 
	{
    	
    		String input = args[0];
	
		try {
		  	//System.out.println("Parsing " + input);
      			parser p = new parser(new IdLexer(new FileReader(input)));
      			//System.out.println(p);
			Program program = (Program)(p.parse().value);
			//System.out.println(program);
			
			Typechecker typechecker = new Typechecker();
      			typechecker.typecheckProgram(program);
      			
    			
    			System.out.println("Done!");
		}
	
		catch(FileNotFoundException e) {
      			System.out.println("File not found!");
			System.exit(1);
    		}
	
		catch(Exception e) {
      			System.out.println(e.getMessage());
			e.printStackTrace();
			System.exit(1);
    		}
	}

} 

