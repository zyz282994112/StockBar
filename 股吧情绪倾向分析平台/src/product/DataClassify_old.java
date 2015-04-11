package product;

import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.classifiers.Evaluation;
import weka.classifiers.misc.InputMappedClassifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.unsupervised.attribute.Remove;

import java.io.File;
import java.io.FileWriter;

/**
* @author zyz
*
*/
public class DataClassify_old {

    public static InputMappedClassifier LoadModel(String name) throws Exception {
        System.out.println("读取模型");
       return (InputMappedClassifier) SerializationHelper.read("D:\\股吧\\model\\"+name+".model");
    }

     public static void TrainModel(String train_name,String classifiere,int num,String[] option) throws Exception {
         Instances train = getFileInstances(train_name);
         //        String[] option=weka.core.Utils.splitOptions("-I -trim -W weka.classifiers.functions.LibSVM -- -S 1");

         train = selectAttUseFilter(num,train);
         InputMappedClassifier  classifier = new InputMappedClassifier();
         classifier.setOptions(option);
         classifier.buildClassifier(train);
         SerializationHelper.write("D:\\股吧\\model\\"+classifiere+".model", classifier);

     }

    public static void main( String[] args ) throws Exception {
        String[] option = weka.core.Utils.splitOptions("weka.classifiers.misc.InputMappedClassifier -trim -W weka.classifiers.functions.LibLINEAR -- -S 1 -C 1.0 -E 0.01 -B 1.0");
        double x = Run(true,"D:\\股吧\\testout\\test.csv",option,"liblinear");
//        System.out.println(x);
//         Train(5000,"liblinear",option);

    }

    public static double Run(boolean istrain, String test_name,String[] option, String modelname) throws Exception {
        if(istrain){
            TrainModel("D:\\股吧\\trainout\\train.csv","liblinear",4000,option);
        }
        return Jisuan(test_name,modelname);
//      return Jisuan_onlyone(test_name,modelname,0);
    }

    public static void Train(int num,String name,String[] option) throws Exception {
            TrainModel("D:\\股吧\\trainout\\train.csv",name,num,option);
    }

    public static double Jisuan(String test_name,String modelname) throws Exception {
        Instances test = getFileInstances("D:\\股吧\\testout\\"+test_name);
        InputMappedClassifier classifier = LoadModel(modelname);
        System.out.println("加载模型：" + modelname + "成功!");
        File writefile=new File("D:\\股吧\\testout\\fenleijieguo_"+test_name+".csv");
        FileWriter writer=new FileWriter(writefile);
        double duo=0,kong=0,fenlei=0;
        for(Instance ins:test){
            fenlei = classifier.classifyInstance(ins);
            if(fenlei==0){
                duo++;
                writer.write("kanduo\r\n");
            }

            else if(fenlei==1){
                writer.write("kankong\r\n");
                kong++;
            }
            else
                writer.write("zaosheng\r\n");
        }
        writer.close();
        return duo>kong?Math.log(1+(duo-kong)/(duo+kong)):-Math.log(1+(kong-duo)/(duo+kong));
    }

    public static double Jisuan_onlyone(String test_name,String modelname,Integer count) throws Exception {
        Instances test = getFileInstances("D:\\股吧\\testout\\"+test_name);
        InputMappedClassifier classifier = LoadModel(modelname);
        System.out.println("加载模型：" + modelname + "成功!");
        File writefile=new File("D:\\股吧\\testout\\fenleijieguo_"+test_name+".csv");
        FileWriter writer=new FileWriter(writefile);
        double duo=0,kong=0,fenlei=0;
        for(Instance ins:test){
            fenlei = classifier.classifyInstance(ins);
            count++;
            writer.write(count.toString()+",");
            if(fenlei==0){
                duo++;
                writer.write("kanduo\r\n");
            }
            else if(fenlei==1){
                writer.write("kankong\r\n");
                kong++;
            }
            else
                writer.write("zaosheng\r\n");
        }
        writer.close();
        return duo>kong?Math.log(1+(duo-kong)/(duo+kong)):-Math.log(1+(kong-duo)/(duo+kong));
    }


    private static Instances getFileInstances(String fileName) throws Exception {
        Instances model_instances = new DataSource(fileName).getDataSet();
        model_instances.setClassIndex( model_instances.numAttributes() - 1 );
        return model_instances;
    }

    private static Instances selectAttUseFilter(int savenum,Instances train) throws Exception {
        AttributeSelection filter = new AttributeSelection();  // package weka.filters.supervised.attribute!
        ASEvaluation eval = new InfoGainAttributeEval();
        Ranker search = new Ranker();
        filter.setEvaluator(eval);
        filter.setSearch(search);
        filter.setInputFormat(train);
        Instances new_train = Filter.useFilter( train, filter);

        Remove remove = new Remove();//非监督学习过滤
        remove.setAttributeIndices((savenum + 1) + "-" + (new_train.numAttributes() - 1));
        remove.setInvertSelection(false);
        remove.setInputFormat(new_train);
        new_train = Filter.useFilter(new_train, remove);

        return new_train;
    }

    public static void pinggu(String fenleiqiname,String train_name,String test_name) throws Exception {
        InputMappedClassifier classifier = LoadModel(fenleiqiname);
        Instances train = getFileInstances(train_name);
       Evaluation testEvaluation = new Evaluation(train);
//        testEvaluation.crossValidateModel(classifier,train,10,new Random());
        Instances test = getFileInstances(test_name);
        testEvaluation.evaluateModel(classifier, test);
        System.out.println(testEvaluation.toSummaryString());
        System.out.println(testEvaluation.toClassDetailsString());
        System.out.println(testEvaluation.toMatrixString());
    }

}






