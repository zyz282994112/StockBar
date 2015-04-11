package product;

import weka.classifiers.Evaluation;
import weka.classifiers.misc.InputMappedClassifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils.DataSource;

import java.io.File;
import java.io.FileWriter;
import java.util.Random;

/**
* @author zyz
*
*/
public class DataClassify {

    public static InputMappedClassifier LoadModel(String name) throws Exception {
        weka.core.SerializationHelper.read("D:\\股吧\\model\\"+name+".model");
       return (InputMappedClassifier)weka.core.SerializationHelper.read("D:\\股吧\\model\\"+name+".model");
    }

     public static void Train(String train_path,String modelname,String[] option) throws Exception {
         Instances train = getFileInstances(train_path);
         InputMappedClassifier  classifier = new InputMappedClassifier();
         classifier.setOptions(option);
         classifier.buildClassifier(train);
         SerializationHelper.write("D:\\股吧\\model\\"+modelname+".model", classifier);
     }

    public static String tmptest(String train_path,String modelname,String savenum,String[] option) throws Exception {
        Instances train = getFileInstances(train_path);
        InputMappedClassifier  classifier = new InputMappedClassifier();
        classifier.setOptions(option);
//        classifier.buildClassifier(train);
        Evaluation testEvaluation = new Evaluation(train);
        testEvaluation.crossValidateModel(classifier, train, 10, new Random(1));
        SerializationHelper.write("D:\\股吧\\model\\"+modelname+savenum+".model", classifier);
        return testEvaluation.toClassDetailsString();
    }

    public static String tmptest2(String train_path,String test_path,String modelname,String savenum,String[] option) throws Exception {
        Instances train = getFileInstances(train_path);
        InputMappedClassifier  classifier = new InputMappedClassifier();
        classifier.setOptions(option);
        classifier.buildClassifier(train);
        Evaluation testEvaluation = new Evaluation(train);
        Instances test = getFileInstances(test_path);
        testEvaluation.evaluateModel(classifier, test);
        SerializationHelper.write("D:\\股吧\\model\\"+modelname+savenum+".model", classifier);
        return testEvaluation.toClassDetailsString();
    }

    public static String[] SelectModel(String Classifier,Integer savenum)throws Exception{
        String[] option=null;
        Classifier=Classifier.toUpperCase();
        if (Classifier.equals("LIBLINEAR")) {
            option = weka.core.Utils.splitOptions("weka.classifiers.misc.InputMappedClassifier -I -trim -W " +
                    "weka.classifiers.meta.AttributeSelectedClassifier -- -E \"" +
                    "weka.attributeSelection.InfoGainAttributeEval \" -S \"" +
                    "weka.attributeSelection.Ranker -T -1.7976931348623157E308 -N "+savenum.toString()+"\" -W " +
                    "weka.classifiers.functions.LibLINEAR -- -S 1 -C 1.0 -E 0.01 -B 1.0"
            );

        } else if (Classifier.equals("LIBSVM")) {
            option = weka.core.Utils.splitOptions("weka.classifiers.misc.InputMappedClassifier -I -trim -W " +
                    "weka.classifiers.meta.AttributeSelectedClassifier -- -E \"" +
                    "weka.attributeSelection.InfoGainAttributeEval \" -S \"" +
                    "weka.attributeSelection.Ranker -T -1.7976931348623157E308 -N "+savenum.toString()+"\" -W " +
                    "weka.classifiers.functions.LibSVM -- -S 1 -K 2 -D 3 -G 0.2 -R 0.0 -N 0.5 -M 40.0 -C 1.0 -E 0.001 -P 0.1 -model \"" +
                    "C:\\\\Program Files\\\\Weka-3-7\"");

        } else {

        }

        return option;
    }

    public static void main( String[] args ) throws Exception {
        String[] option = SelectModel("liblinear",4000);
        double x = Run(true,"D:\\股吧\\testout\\test.csv",option,"liblinear");
        System.out.println(x);
//         Train(5000,"liblinear",option);
    }

    public static double Run(boolean istrain, String test_name,String[] option, String modelname) throws Exception {//待修改
        if(istrain)
            Train("D:\\股吧\\trainout\\train.csv", modelname, option);
        return Jisuan(test_name,modelname);
    }

    public static double Jisuan(String test_name,String modelname) throws Exception {
        Instances test = getFileInstances("D:\\股吧\\testout\\"+test_name);
        if(test!=null) System.out.println("加载数据成功！"+(Integer)test.size());
        System.out.println(test.classAttribute().value(0) + test.classAttribute().value(1) + test.classAttribute().value(2));
        InputMappedClassifier classifier = LoadModel(modelname);
        System.out.println("加载模型：" + modelname + "成功!");

        File writefile=new File("D:\\股吧\\testout\\fenleijieguo_"+test_name);
        FileWriter writer=new FileWriter(writefile);
        double duo=0,kong=0,fenlei= 3;
        for(Instance ins:test){
            fenlei = classifier.classifyInstance(ins);
            writer.write(test.classAttribute().value((int)fenlei)+"\r\n");
        }
        writer.close();
        return duo>kong?Math.log(1+(duo-kong)/(duo+kong)):-Math.log(1+(kong-duo)/(duo+kong));
    }

    private static Instances getFileInstances(String fileName) throws Exception {
        Instances model_instances = new DataSource(fileName).getDataSet();
        model_instances.setClassIndex( model_instances.numAttributes() - 1 );
        return model_instances;
    }

    public static void pinggu(boolean iscross, String Classifier,String train_path,String test_path) throws Exception {

        Instances train = getFileInstances(train_path);//非常奇怪，这一句和下面换个位置就会报错！
        InputMappedClassifier classifier = LoadModel(Classifier);
       Evaluation testEvaluation = new Evaluation(train);
        if(iscross)
            testEvaluation.crossValidateModel(classifier,train,10,new Random(1));
        else {
            Instances test = getFileInstances(test_path);
            testEvaluation.evaluateModel(classifier, test);
        }

        System.out.println(testEvaluation.toSummaryString());
        System.out.println(testEvaluation.toClassDetailsString());
        System.out.println(testEvaluation.toMatrixString());
    }

}






