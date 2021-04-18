import java.io.*;
import java.nio.charset.Charset;

import org.deidentifier.arx.Data;
import org.deidentifier.arx.ARXConfiguration;
import org.deidentifier.arx.DataType;
import org.deidentifier.arx.AttributeType;
import org.deidentifier.arx.AttributeType.Hierarchy;
import  org.deidentifier.arx.AttributeType.Hierarchy.DefaultHierarchy;
import org.deidentifier.arx.ARXLattice.ARXNode;
import org.deidentifier.arx.criteria.KAnonymity;
import org.deidentifier.arx.ARXAnonymizer;
import org.deidentifier.arx.ARXResult;
import org.deidentifier.arx.DataHandle;
import org.deidentifier.arx.criteria.EqualDistanceTCloseness;

/**
 * DataProcess class accepts path of dataset file from std I/O, 
 * and executes the k-anonymity algorithm on the dataset,
 * then write the result as a new dataset file to the specified path.
 */
public class DataProcess{
    
    public static void main(String[] argv){
        // FIXME: Do not use hard code, pass by arguments instead.
        // Loading data from path.
        String inputPath = argv[0];
        File inputFile = new File(inputPath);
        File outputFile = new File("../resources/data/result.csv");

        try{
            Data data = Data.create(inputFile, Charset.defaultCharset(),',');
            DataHandle dataHandler = data.getHandle();
            String[] gainList = dataHandler.getDistinctValues(dataHandler.getColumnIndexOf("capital-gain"));
            String[] lossList = dataHandler.getDistinctValues(dataHandler.getColumnIndexOf("capital-loss"));
            String[] hoursList = dataHandler.getDistinctValues(dataHandler.getColumnIndexOf("hours-per-week"));
            String[] occupationList = dataHandler.getDistinctValues(dataHandler.getColumnIndexOf("occupation"));
            
            // Definne hierarchies for quasi-attributes
            // Define hierarchies for ages
            DefaultHierarchy age = Hierarchy.create();
            for(Integer i = 0; i < 120; i++){
                Integer btm = i > 30 ? ((i - 30)/30)*30 : 0;
                Integer top = i > 30 ? (i/30)*30 : 30;
                age.add(i.toString(), btm.toString()+ "-" +top.toString(), "*");
            }

            // Define hierarchies for captial-gain
            DefaultHierarchy gain = Hierarchy.create();
            for(String item : gainList){
                Integer val = Integer.valueOf(item);
                Integer btm = val > 2000 ? ((val - 2000)/2000)*2000 : 0;
                Integer top = val > 2000 ? (val/2000)*2000 : 2000;
                gain.add(item, btm.toString()+ "-" +top.toString(), "*");
            }

            // Define hierarchies for captial-loss
            DefaultHierarchy loss = Hierarchy.create();
            for(String item : lossList){
                Integer val = Integer.valueOf(item);
                Integer btm = val > 2000 ? ((val - 2000)/2000)*2000 : 0;
                Integer top = val > 2000 ? (val/2000)*2000 : 2000;
                loss.add(item, btm.toString()+ "-" +top.toString(), "*");
            }

            // Define hierarchies for hours-per-week
            DefaultHierarchy hours = Hierarchy.create();
            for(String item : hoursList){
                Integer val = Integer.valueOf(item);
                Integer btm = val > 20 ? ((val - 20)/20)*20 : 0;
                Integer top = val > 20 ? (val/20)*20 : 20;
                hours.add(item, btm.toString()+ "-" +top.toString(), "*");
            } 
            
            // Define hierarchies for occupations
            DefaultHierarchy occupation = Hierarchy.create();
            for(String item : occupationList){
                occupation.add(item, "*");
            }

            // Generalization, defining attribute types
            // This fnlwgt should be considered as unique identifier.
            data.getDefinition().setAttributeType("fnlwgt", AttributeType.IDENTIFYING_ATTRIBUTE);

            // Assuming that Quasi-identifier[age, workclass, captial-gain, captial-loss, hours-per-week] 
            // could be used by linking with external datasets to uniquely identify a record in this dataset.
            data.getDefinition().setAttributeType("age", age);
            data.getDefinition().setAttributeType("occupation", occupation);
            data.getDefinition().setAttributeType("capital-gain", gain);
            data.getDefinition().setAttributeType("capital-loss", loss);
            data.getDefinition().setAttributeType("hours-per-week", hours);

            // // The following attributes could be considered as insensitive attributes, as they are less
            // // likely to be used as quasi-identifier.
            data.getDefinition().setAttributeType("workclass", AttributeType.INSENSITIVE_ATTRIBUTE);
            data.getDefinition().setAttributeType("education", AttributeType.INSENSITIVE_ATTRIBUTE);
            data.getDefinition().setAttributeType("marital-status", AttributeType.INSENSITIVE_ATTRIBUTE);
            data.getDefinition().setAttributeType("relationship", AttributeType.INSENSITIVE_ATTRIBUTE);
            data.getDefinition().setAttributeType("race", AttributeType.INSENSITIVE_ATTRIBUTE);
            data.getDefinition().setAttributeType("gender", AttributeType.INSENSITIVE_ATTRIBUTE);
            data.getDefinition().setAttributeType("native-country", AttributeType.INSENSITIVE_ATTRIBUTE);
            data.getDefinition().setAttributeType("income", AttributeType.INSENSITIVE_ATTRIBUTE); 

            // Set privacy models and transformation rules
            ARXConfiguration config = ARXConfiguration.create();
            config.addPrivacyModel(new KAnonymity(2));

            // Executing the algorithm
            ARXAnonymizer anonymizer = new ARXAnonymizer();
            ARXResult result = anonymizer.anonymize(data, config);

            // Compare data with original data
            if(result.isResultAvailable()){
                DataHandle outputHandler = result.getOutput();
                outputHandler.save(outputFile,',');
            }else{
                throw new NullPointerException();
            }

        }catch(Exception e){
            System.out.println("Catch exception: "+e);
        }
    }
}