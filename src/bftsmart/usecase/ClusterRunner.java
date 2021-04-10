package bftsmart.usecase;

import bftsmart.runtime.RMIRuntime;

import java.io.*;

public class ClusterRunner {

    public void executeCommands(String configPath, Integer reps, String numberOfFaults) throws IOException, InterruptedException {

            File f = new File(configPath);
            String configName = f.getName();

            int totalNumberOfHosts = 1;
            for(String hostConf : configName.split("-"))
            {
                String val = hostConf.substring(1);
                Integer fSize = Integer.valueOf(hostConf.substring(1));
                totalNumberOfHosts += (3*fSize)+1;
            }
            File deployScript = createDeployScript(f.getName(), totalNumberOfHosts, numberOfFaults);

            String command = "sbatch " + deployScript.toString() +  " systemconfig" + System.getProperty("file.separator") + f.getName();
            System.out.println(command);
            for(int i = 0; i < reps; i++) {
                ProcessBuilder pb = new ProcessBuilder("sbatch", deployScript.toString(), configPath);
                pb.inheritIO();
                Process p = pb.start();

                Thread.sleep(3 * 60 * 1000);

                ProcessBuilder pb2 = new ProcessBuilder("grep", "Average", totalNumberOfHosts - 1 + ".log");
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

    public File createDeployScript(String configFileName, int numberOfHosts, String numberOfFaults){
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
            /* arguments of the template deploy script:
            1) total number of nodes
            2) total number of nodes
            3) number of faults
            4) number of faults
             */
            file = String.format(file, numberOfHosts, numberOfHosts, numberOfFaults, numberOfFaults);
            String fileName = "deploy-" + configFileName + ".sh";
            File deployScript = new File(fileName);
//            if(!deployScript.exists()) {
//                PrintWriter systemConfigWriter = new PrintWriter(fileName, "UTF-8");
                Writer streamWriter = new OutputStreamWriter(new FileOutputStream(deployScript));
                PrintWriter printWriter = new PrintWriter(streamWriter);
                printWriter.write(file);
                printWriter.flush();
//            }
            return deployScript;
        }
        catch (IOException e)
        {
            System.out.println("Cannot read system config template file");
        }
        return null;
    }

    /**
     *
     * @param args [0] is the config path of the use-case (in systemconfig folder)
     * @param args [1] is the number of repetitions
     * @param args [2] number of faults (0 : max)
     * @throws IOException
     * @throws InterruptedException
     */
    public static void main(String[] args) throws IOException, InterruptedException {
//        if(args[2].equals("max"))
//            RMIRuntime.NUMBER_OF_FAULTS = Integer.MAX_VALUE;
//        else
//            RMIRuntime.NUMBER_OF_FAULTS = Integer.valueOf(args[2]);
        ClusterRunner runner = new ClusterRunner();
        runner.executeCommands(args[0], Integer.parseInt(args[1]), args[2]);
    }
}
