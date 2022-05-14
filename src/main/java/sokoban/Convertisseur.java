package sokoban;

import java.util.Scanner;
import java.util.Arrays;
import java.io.*;
import java.text.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import fr.uga.pddl4j.parser.ErrorManager;
import fr.uga.pddl4j.parser.Message;
import fr.uga.pddl4j.parser.PDDLParser;
import fr.uga.pddl4j.parser.ParsedProblem;
import fr.uga.pddl4j.problem.ADLProblem;
import fr.uga.pddl4j.problem.operator.Action;
import fr.uga.pddl4j.plan.Plan;
import fr.uga.pddl4j.problem.operator.ConditionalEffect;
import fr.uga.pddl4j.problem.operator.Condition;
import fr.uga.pddl4j.problem.numeric.NumericConstraint;


import fr.uga.pddl4j.heuristics.state.StateHeuristic;
import fr.uga.pddl4j.planners.LogLevel;
import fr.uga.pddl4j.planners.statespace.HSP;

import java.io.FileNotFoundException;

import java.io.FileNotFoundException;

public class Convertisseur{
    static private File filePddl;
    static private String path;

    static public String ConvertionJsonPddl(String JsonFile) throws NullPointerException{
        try{
            path = System.getProperty("user.dir");

            filePddl = new File(path+"/problem.pddl");

            // TODO trouver le chemin relatif
            Reader JSONfile = new FileReader(path+"/config/"+JsonFile);
            JSONParser parser = new JSONParser();

            JSONObject object = (JSONObject) parser.parse(JSONfile);
            String mapJSON = (String) object.get("testIn");
            System.out.println("JSONmap: \n"+mapJSON);

            // On trouve les dimmensions
            int lig = 1;
            int col = 0;
            int colmax = 0;
            char c;
            CharacterIterator it = new StringCharacterIterator(mapJSON);
            while((c=it.current()) != CharacterIterator.DONE){
                if(c == '\n'){
                    lig++;
                    if(col > colmax){
                        colmax = col;
                    }
                    col = 0;
                }
                else{
                    col++;
                }
                it.next();
            }
            colmax++;
            // On cree et rempli le tableau
            char[][] carte = new char[lig][colmax];
            //System.out.println("lig = "+lig+"\ncol = "+col);
            it = new StringCharacterIterator(mapJSON);
            boolean in = false;
            lig = 0;
            col = 0;
            while((c=it.current()) != CharacterIterator.DONE){
                if(in == true){
                    if(c != '\n'){
                        carte[lig][col] = c;
                    }
                }else{
                    if(c == '#'){
                        in = true;
                        carte[lig][col] = c;
                    }else{
                        carte[lig][col] = 'X';
                    }
                }
                if(c == '\n'){
                    in = false;
                    lig++;
                    col = 0;
                }
                else{
                    col++;
                }
                it.next();
            }

            lig++;
            col++;
            
            //System.out.println("Lig = "+lig);
            //System.out.println("Col = "+col);

            System.out.println("carte : ");
            for(int i =0;i<lig;i++){
                System.out.println(carte[i]);
            }

            PrintWriter writerPddl = new PrintWriter(filePddl);

            // Passage de la carte au format pddl
            writeHeader(lig,col,writerPddl);

            for(int i = 0;i<lig;i++){
                for(int j=0;j<col;j++){
                    convert(i,j,carte[i][j],writerPddl);
                    if(carte[i][j] != '#' && carte[i][j] != 'X'){
                        voisin(carte,i,j,lig,col,writerPddl);
                    }
                }
            }
            initColonne(lig,col,writerPddl);
            writerPddl.println("\t)\n\t(:goal (and");
            for(int i = 0;i<lig;i++){
                for(int j=0;j<col;j++){
                    if(carte[i][j] == '.'|| carte[i][j] == '+' || carte[i][j] == '*'){
                        writerPddl.println("\t\t(at C v"+i+" h"+j+")");
                    }
                }
            }
            writerPddl.println("\t\t)\n\t)\n)");
            writerPddl.close();
        } catch(Exception e){
            System.out.println("Probleme de convertion du JSON : "+e);
        }
        return "";
    }

    static private void writeHeader(int lig, int col,PrintWriter writerPddl){
        System.out.println("Col dans header = "+col);
        writerPddl.println("(define (problem pb_sokoban)\n"+
                            "\t(:domain sokoban)\n\n"+
                            "\t(:objects\n"+
                            "\t\tJ - joueur\n"+
                            "\t\tC - caisse\n"+
                            "\t\tT - cible\n"+
                            "\t\tV - vide");
        writerPddl.print("\t\t");
        for(int i = 0;i<col;i++){
            writerPddl.print("h"+i+" ");
        }
        writerPddl.print("- horizontal\n\t\t");
        for(int i = 0;i<lig;i++){
            writerPddl.print("v"+i+" ");
        }
        writerPddl.println("- vertical\n\t)\n\t(:init");
    }

    static private void voisin(char[][] carte,int lig,int col,int ligMax,int colMax,PrintWriter writerPddl){
        //writerPddl.println("\t\t(at C v"+lig+" h"+col+")");
        if(lig < ligMax-1 && carte[lig+1][col] != '#' && carte[lig+1][col] != 'X'){
            writerPddl.println("\t\t(estACote v"+lig+" h"+col+" v"+(lig+1)+" h"+col+")");
            writerPddl.println("\t\t(estACote v"+(lig+1)+" h"+col+" v"+lig+" h"+col+")");
        }
        if(col < colMax-1 && carte[lig][col+1] != '#' && carte[lig][col+1] != 'X'){
            writerPddl.println("\t\t(estACote v"+lig+" h"+col+" v"+lig+" h"+(col+1)+")");
            writerPddl.println("\t\t(estACote v"+lig+" h"+(col+1)+" v"+lig+" h"+col+")");
        }
    }

    static private void initColonne(int lig,int col,PrintWriter writerPddl){
        for(int i = 0;i<col;i++){
            writerPddl.println("\t\t(estMemeColonne h"+i+" h"+i+")");
        }
        for(int i = 0;i<lig;i++){
            writerPddl.println("\t\t(estMemeLigne v"+i+" v"+i+")");
        }
    }
    static private void convert(int lig, int col,char c,PrintWriter writerPddl){
        switch(c){
            case '$':
                writerPddl.println("\t\t(at C v"+lig+" h"+col+")");
                writerPddl.println("\t\t(estCaisse v"+lig+" h"+col+")");
                break;
            case '.':
                writerPddl.println("\t\t(at T v"+lig+" h"+col+")");
                break;
            case '*':
                writerPddl.println("\t\t(at C v"+lig+" h"+col+")");
                writerPddl.println("\t\t(at T v"+lig+" h"+col+")");
                writerPddl.println("\t\t(estCaisse v"+lig+" h"+col+")");
                break;
            case '@':
                writerPddl.println("\t\t(at J v"+lig+" h"+col+")");
                break;
            case '+':
                writerPddl.println("\t\t(at J v"+lig+" h"+col+")");
                writerPddl.println("\t\t(at T v"+lig+" h"+col+")");
                break;
            case ' ':
                writerPddl.println("\t\t(at V v"+lig+" h"+col+")");
                break;
            default:

        }
    }

    static public String getSolution(){
        String res = "";
        
        try {
            // Creates an instance of the PDDL parser
            final PDDLParser parser = new PDDLParser();
            // Parses the domain and the problem files.
            final ParsedProblem parsedProblem = parser.parse(path + "/sokoban.pddl", path + "/problem.pddl");
            // Gets the error manager of the parser
            final ErrorManager errorManager = parser.getErrorManager();
            // Checks if the error manager contains errors
            if (!errorManager.isEmpty()) {
                // Prints the errors
                for (Message m : errorManager.getMessages()) {
                    System.out.println(m.toString());
                }
            } else {
                // Prints that the domain and the problem were successfully parsed
                System.out.print("\nparsing domain file \"" + path + "/sokoban.pddl" + "\" done successfully");
                System.out.print("\nparsing problem file \"" +path + "/problem.pddl" + "\" done successfully\n\n");
                // Create a ADL problem
                final ADLProblem problem = new ADLProblem(parsedProblem);
                // Instantiate the planning problem
                problem.instantiate();
                /*
                // Print the list of actions of the instantiated problem
                for (Action a : problem.getActions()) {
                    System.out.println(problem.toString(a));
                }
                //*/
                HSP planner = new HSP();

                Plan plan = planner.solve(problem);
                System.out.println("\nXXXXXXXXXXXXXXXXXXXXXXXX\nPlan de solution :\nXXXXXXXXXXXXXXXXXXXXXXXX\n");
                
                String StringPlan = problem.toString(plan);
                System.out.println(StringPlan);
                String[] array = StringPlan.split("\\n");
                for(String s : array){
                    if(s.contains("movecaissecolonne")){
                        int iv0, v0, iv1, v1;
                        iv0 = s.indexOf(" v");
                        iv1 = s.indexOf(" v",iv0+1);
                        v0 = Integer.parseInt(s.substring(iv0+2,iv0+3));
                        v1 = Integer.parseInt(s.substring(iv1+2,iv1+3));
                        if(v0<v1){
                            res+="D";
                        }else{
                            res+="U";
                        }

                    }else if(s.contains("movecaisseligne")){
                        int ih0, h0, ih1, h1;
                        ih0 = s.indexOf(" h");
                        ih1 = s.indexOf(" h",ih0+1);
                        h0 = Integer.parseInt(s.substring(ih0+2,ih0+3));
                        h1 = Integer.parseInt(s.substring(ih1+2,ih1+3));
                        if(h0>h1){
                            res+="L";
                        }else{
                            res+="R";
                        }
                    }else { // move
                        int iv0, v0, iv1, v1;
                        iv0 = s.indexOf(" v");
                        iv1 = s.indexOf(" v",iv0+1);
                        v0 = Integer.parseInt(s.substring(iv0+2,iv0+3));
                        v1 = Integer.parseInt(s.substring(iv1+2,iv1+3));
                        //System.out.println("v0, v1 :"+v0+", "+v1);
                        if(v0<v1){
                            //System.out.println("D");
                            res+="D";
                        }else if(v0>v1){
                            //System.out.println("U");
                            res+="U";
                        }
                        int ih0, h0, ih1, h1;
                        ih0 = s.indexOf(" h");
                        ih1 = s.indexOf(" h",ih0+1);
                        h0 = Integer.parseInt(s.substring(ih0+2,ih0+3));
                        h1 = Integer.parseInt(s.substring(ih1+2,ih1+3));
                        //System.out.println("h0, h1 :"+h0+", "+h1);
                        if(h0>h1){
                            res+="L";
                        }else if(h0<h1){
                            res+="R";
                        }
                    }
                }

                System.out.println("\nXXXXXXXXXXXXXXXXXXXXXXXX\nXXXXXXXXXXXXXXXXXXXXXXXX\n");
            }
            // This exception could happen if the domain or the problem does not exist
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        return res;
    }

}
