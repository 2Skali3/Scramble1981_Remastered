package scramble.model.map.utils.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import scramble.model.common.impl.PairImpl;
import scramble.model.map.utils.api.CSVReader;

public class CSVReaderImpl implements CSVReader <PairImpl<Integer,CSVReaderImpl.Behaviour>> {

    /**
     * enum che descrive i comportamenti che il terreno può adorrate
     */
    public static enum Behaviour {
        /**
         * terreno piatto
         */
        FLAT,
        
        /**
         * terreno crescente verso l'alto
         */
        UP, 

        /**
         * terreno calante verso il basso
         */
        DW, 

        /**
         * terreno a punta
         */
        SUMMIT, 

        /**
         * assenza di terreno
         */
        EMPTY
    }

    /**
     * enum che descrive gli elementi composti da terreno.
     */
    public static enum StageComponent {
        FLOOR, CEILING
    }
    
    public ArrayList<PairImpl<Integer,Behaviour>> readCSV(final String fileRelativePath) {

        ArrayList<PairImpl<Integer,Behaviour>> dataLandscape = new ArrayList<>();

        ClassLoader classLoader = CSVReaderImpl.class.getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(fileRelativePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            Behaviour behaviour = Behaviour.DW;
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int length = Integer.parseInt(parts[0].trim());
                behaviour = Behaviour.valueOf(parts[1].trim());
                dataLandscape.add(new PairImpl<Integer,CSVReaderImpl.Behaviour>(length, behaviour));
                System.out.println(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return dataLandscape;
    }

}
