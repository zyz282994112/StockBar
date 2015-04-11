package test;

import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.misc.InputMappedClassifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.RemoveByName;

import java.io.File;
import java.util.regex.Pattern;

/**
* @author zyz
*
*/
public class AttributeSelectionTest {


    public static Instances m_instances = null;
    private Instances test_instances = null;
    private Instances selectedIns = null;
    private static Pattern pattern = Pattern.compile("0.[0-9]{3}");

    public static void main( String[] args ) throws Exception {
        AttributeSelectionTest filter = new AttributeSelectionTest();
        String[] option=weka.core.Utils.splitOptions("-I -trim -W weka.classifiers.functions.LibSVM -- -S 1 -K" +
                " 2 -D 3 -G 0.0 -R 0.0 -N 0.5 -M 40.0 -C 1.0 -E 0.001 -P 0.1 -model \"C:\\Program Files\\Weka-3-7\"");

        filter.getFileInstances("D:\\股吧\\trainout\\train.csv","D:\\股吧\\testout\\test.csv");
        filter.selectAttUseFilter(5000);
        InputMappedClassifier  classifier = new InputMappedClassifier();
        classifier.setOptions(option);
        filter.Classify(classifier);

    }

    public void getFileInstances(String fileName, String testFileName) throws Exception {
        DataSource frData = new DataSource(fileName);
        m_instances = frData.getDataSet();
        m_instances.setClassIndex( m_instances.numAttributes() - 1 );
        DataSource frData1 = new DataSource( testFileName );
        test_instances = frData1.getDataSet();
        test_instances.setClassIndex( test_instances.numAttributes() - 1 );
    }

    public void selectAttUseFilter(int savenum) throws Exception {
        AttributeSelection filter = new AttributeSelection();  // package weka.filters.supervised.attribute!
        ASEvaluation eval = new InfoGainAttributeEval();
        Ranker search = new Ranker();
        filter.setEvaluator(eval);
        filter.setSearch(search);
        filter.setInputFormat(m_instances);
        System.out.println("number of instance attribute = " + m_instances.numAttributes());
        selectedIns = Filter.useFilter( m_instances, filter);

        Remove remove = new Remove();//非监督学习过滤
        remove.setAttributeIndices((savenum+1)+"-" + (selectedIns.numAttributes() - 1));
        remove.setInvertSelection(false);
        remove.setInputFormat(selectedIns);
        selectedIns = Filter.useFilter(selectedIns, remove);

        RemoveByName removebianhao = new RemoveByName();//删除编号
        removebianhao.setExpression("bianhao");
        removebianhao.setInvertSelection(false);
        removebianhao.setInputFormat(selectedIns);
        selectedIns = Filter.useFilter(selectedIns, removebianhao);
        System.out.println( "number of selected instance attribute = " + selectedIns.numAttributes());
    }

    public void Classify(Classifier classifier) throws Exception {
        classifier.buildClassifier(selectedIns);

       Evaluation testEvaluation = new Evaluation(selectedIns);
//       testEvaluation.crossValidateModel(classifier,selectedIns,10,new Random(1));
        testEvaluation.evaluateModel(classifier, test_instances);
        System.out.println(testEvaluation.toSummaryString()+testEvaluation.toString());
        System.out.println(testEvaluation.toClassDetailsString());


//        for(int i=0;i<test_instances.size();i++){
//            double xx=classifier.classifyInstance(test_instances.get(i));
//            System.out.println("预测" + xx + "实际" + test_instances.get(i).classValue() + test_instances.get(i).stringValue(test_instances.numAttributes() - 1));
//        }

    }

}






