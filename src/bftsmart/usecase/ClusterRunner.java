package bftsmart.usecase;

import java.io.*;

public class ClusterRunner {

    public static int REPITITION = 50;

    public void executeCommands() throws IOException, InterruptedException {

        for(File f : new File("systemconfig").listFiles()) {
            if(!f.getName().contains("ot-"))
                continue;

            String configName = f.getName();
            String conf = configName.substring(configName.indexOf('-')+1);

            int totalNumberOfHosts = 1;
            for(String hostConf : conf.split("-"))
            {
                String val = hostConf.substring(1);
                Integer fSize = Integer.valueOf(hostConf.substring(1));
                totalNumberOfHosts += (3*fSize)+1;
            }

            File deployScript = createTempScript(f.getName(), totalNumberOfHosts);
            if(deployScript == null)
            {
                System.out.println("created deploy script is null for " + f.getName());
                continue;
            }
            String command = "sbatch " + deployScript.toString() +  " systemconfig" + System.getProperty("file.separator") + f.getName();
            System.out.println(command);
            for(int i = 0; i < 2; i++) {
                ProcessBuilder pb = new ProcessBuilder("sbatch", deployScript.toString(), "systemconfig" + System.getProperty("file.separator") + f.getName());
//            ProcessBuilder pb = new ProcessBuilder("ls","-lash");
                pb.inheritIO();
                Process p = pb.start();

//            InputStreamReader inputStreamReader = new InputStreamReader(p.getInputStream());
//            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//            String output = null;
//            while ((output = bufferedReader.readLine()) != null) {
//                System.out.println(output);
//            }

                Thread.sleep((30 + ClusterRunner.REPITITION * 2) * 1000 + 20000);

                ProcessBuilder pb2 = new ProcessBuilder("tail", "-1", totalNumberOfHosts - 1 + ".log");
                pb2.inheritIO();
                Process p2 = pb2.start();

                InputStreamReader inputStreamReader2 = new InputStreamReader(p2.getInputStream());
                BufferedReader bufferedReader2 = new BufferedReader(inputStreamReader2);
                String output2 = null;
                while ((output2 = bufferedReader2.readLine()) != null) {
                    System.out.println(output2);
                }

                p2.waitFor();


                ProcessBuilder pb3 = new ProcessBuilder("scancel", "-u", "fhous001");
                pb3.inheritIO();
                Process p3 = pb3.start();
                p3.waitFor();

                Thread.sleep(2000);
            }
        }
    }

    public File createTempScript(String configFileName, int numberOfHosts){
        try
        {
            FileReader fr = new FileReader("systemconfig/deploy.sh");
            BufferedReader rd = new BufferedReader(fr);
            String line = null;
            String file = "";
            while ((line = rd.readLine()) != null) {
                file += line;
                file += "\n";
            }
            fr.close();
            rd.close();
            /* arguments of the template system.config file:
            1) total number of nodes
            2) total number of nodes
             */
            file = String.format(file, numberOfHosts, numberOfHosts);
            String fileName = "deploy-" + configFileName + ".sh";
            File deployScript = new File(fileName);
            if(!deployScript.exists()) {
//                PrintWriter systemConfigWriter = new PrintWriter(fileName, "UTF-8");
                Writer streamWriter = new OutputStreamWriter(new FileOutputStream(deployScript));
                PrintWriter printWriter = new PrintWriter(streamWriter);
                printWriter.write(file);
                printWriter.flush();
            }
            return deployScript;
        }
        catch (IOException e)
        {
            System.out.println("Cannot read system config template file");
        }
        return null;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        ClusterRunner runner = new ClusterRunner();
        runner.executeCommands();
    }
}
