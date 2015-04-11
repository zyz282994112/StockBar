package test;

import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.misc.InputMappedClassifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.RemoveByName;

import java.util.Map;
import java.util.TreeMap;

/**
* @author zyz
*
*/
public class DataClassify_old {

    private static int zaoshengvalue = 2;
    private static Map<Integer,Double> filter = new TreeMap<Integer, Double>();

    public static void Train_Classifiy(Instances train,String name,int savenum) throws Exception {
        String[] option=weka.core.Utils.splitOptions("-I -trim -W weka.classifiers.functions.LibSVM -- -S 1");
        train = selectAttUseFilter(savenum,train);
        InputMappedClassifier  classifier = new InputMappedClassifier();
        classifier.setOptions(option);
        classifier.buildClassifier(train);
        SerializationHelper.write("D:\\股吧\\model\\"+name+".model", classifier);

    }

    public static InputMappedClassifier LoadModel(String name) throws Exception {
       return (InputMappedClassifier) SerializationHelper.read("D:\\股吧\\model\\"+name+".model");
    }

     public static void TrainModel(String train_duan_name,String train_name,int num_duan,int num) throws Exception {
         Instances train = getFileInstances(train_name);
         Instances train_duan = getFileInstances(train_duan_name);

         Train_Classifiy(train_duan, "SVM_DUAN",num_duan);
         Train_Classifiy(train, "SVM",num);

     }

    public static void main( String[] args ) throws Exception {


//
//        double x = Run(true,"D:\\股吧\\testout\\test_filter.csv");
//        System.out.println(x);

        Train(5000);

    }

    public static double Run(boolean istrain, String test_name) throws Exception {
        if(!istrain)
            TrainModel("D:\\股吧\\trainout\\train_filter.csv","D:\\股吧\\trainout\\train.csv",5000,5000);
        return Jisuan(test_name);
    }

    public static void Train(int num) throws Exception {
            TrainModel("D:\\股吧\\trainout\\train_filter.csv","D:\\股吧\\trainout\\train.csv",num,num);
    }

    public static double Jisuan(String test_name) throws Exception {
        Instances test = getFileInstances(test_name);
        Instances test_new = ZaoshengShaixuan(LoadModel("SVM_DUAN"),test);
        test_new = HuanYuan(test_new);
        InputMappedClassifier classifier = LoadModel("SVM");
        double duo=0,kong=0,fenlei=0;
        for(Instance ins:test_new){
            fenlei = (int)classifier.classifyInstance(ins);
            if(fenlei==0)
                duo++;
            else if(fenlei==1)
                kong++;
            System.out.println(ins.value(0)+":"+fenlei);
        }

        return duo>kong?Math.log(1+(duo-kong)/(duo+kong)):-Math.log(1+(kong-duo)/(duo+kong));
    }

    private static Instances getFileInstances(String fileName) throws Exception {
        DataSource frData = new DataSource(fileName);
        Instances model_instances = frData.getDataSet();
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
        remove.setAttributeIndices((savenum+1)+"-" + (new_train.numAttributes() - 1));
        remove.setInvertSelection(false);
        remove.setInputFormat(new_train);
        new_train = Filter.useFilter(new_train, remove);

        RemoveByName removebianhao = new RemoveByName();//删除编号
        removebianhao.setExpression("NO");
        removebianhao.setInvertSelection(false);
        removebianhao.setInputFormat(new_train);
        new_train = Filter.useFilter(new_train, removebianhao);

        return new_train;
    }

    private static Instances ZaoshengShaixuan(Classifier classifier,Instances test_instances) throws Exception {
        Instances test_new = new Instances(test_instances);
        for(Instance entry:test_new)
           if((int)classifier.classifyInstance(entry)==zaoshengvalue){
               filter.put((int)entry.value(0),entry.classValue());
               test_new.remove(entry);
           }

        return test_new;
    }

    private static Instances HuanYuan(Instances test) throws Exception{
        for(int i=0;i<test.size();i++)
            for(int j=i+1;j<test.size();){
                if(test.get(i).value(0)==test.get(j).value(0)){
                    for(int k=1;k<test.get(i).numAttributes()-1;k++)
                        if(test.get(j).stringValue(k)=="T")
                            test.get(i).setValue(k, "T");
                    test.remove(j);
                }
                else j++;
            }
        return test;
    }

    public static void pinggu(Classifier classifier,Instances train,Instances test) throws Exception {
       Evaluation testEvaluation = new Evaluation(train);
        testEvaluation.evaluateModel(classifier, test);
        System.out.println(testEvaluation.toSummaryString());
        System.out.println(testEvaluation.toClassDetailsString());
        System.out.println(testEvaluation.toMatrixString());
    }

}






