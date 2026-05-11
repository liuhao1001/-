package myproject.common.utils;


import cn.hutool.core.lang.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * - 算法详解
 * 最大熵属于辨识模型,能够满足所有已知的约束, 对未知的信息不做任何过分的假设。
 * 详见[《最大熵的Java实现》][1]
 * 提供提供训练与预测接口。训练算法采用GIS训练算法
 * [1]: http://www.hankcs.com/nlp/maximum-entropy-java-implementation.html
 **/
public class ForecastUtils {
	/**
	 * 样本数据集
	 */
	static List<Instance> instanceList = new ArrayList<Instance>();
	/**
	 * 特征列表，来自所有事件的统计结果
	 */
	static List<Feature> featureList = new ArrayList<Feature>();
	/**
	 * 每个特征的出现次数
	 */
	static List<Integer> featureCountList = new ArrayList<Integer>();
	/**
	 * 事件（类别）集
	 */
	static List<String> labels = new ArrayList<String>();
	/**
	 * 每个特征函数的权重
	 */
	static double[] weight;
	/**
	 * 一个事件最多一共有多少种特征
	 */
	static int C;

	public static String forecast(List<Map<String, String>> transData, Map<String, String> forecastValues) {
		loadData(transData);
		train(10);
		List<String> fieldList = new ArrayList<String>();
		for (String key : forecastValues.keySet()) {
			fieldList.add(forecastValues.get(key));
		}
		Pair<String, Double>[] prediction = predict(fieldList);  // 预测
		List<String> topPairs = getTopSeven(prediction);
		clear();
		return topPairs.toString();
	}

	private static void clear() {
		instanceList = new ArrayList<Instance>();
		featureList = new ArrayList<Feature>();
		featureCountList = new ArrayList<Integer>();
		labels = new ArrayList<String>();
		weight = new double[weight.length];
		C = 0;
	}

	/**
	 * 加载数据，并且创建如下域
	 * featureList：特征函数的list
	 * featureCountList:与特征函数一一对应的，特征函数出现的次数
	 * instanceList:样本数据list
	 * labels:类别list
	 *
	 * @param transData
	 * @throws IOException
	 */
	public static void loadData(List<Map<String, String>> transData) {
		for (Map<String, String> transDatum : transData) {
			List<String> fieldList = new ArrayList<String>();
			String label = "";
			for (String key : transDatum.keySet()) {
				if ("result".equals(key)) {
					label = transDatum.get("result");
					break;
				}
			}
			for (String key : transDatum.keySet()) {
				if ("result".equals(key)) {
					continue;
				}
				fieldList.add(transDatum.get(key));
				Feature feature = new Feature(label, transDatum.get(key));
				int index = featureList.indexOf(feature);
				if (index == -1) {
					featureList.add(feature);
					featureCountList.add(1);
				} else {
					featureCountList.set(index, featureCountList.get(index) + 1);
				}
			}
			if (fieldList.size() > C) C = fieldList.size();
			Instance instance = new Instance(label, fieldList);
			instanceList.add(instance);
			if (!labels.contains(label)) labels.add(label);
		}
	}

	public static List<String> getTopSeven(Pair<String, Double>[] pairs) {
		// 使用自定义比较器进行排序
		Arrays.sort(pairs, (p1, p2) -> {
			// 将Double值转换为字符串并去除科学计数法
			String str1 = String.format("%f", p1.getValue()).replaceAll("e", "\\$e");
			String str2 = String.format("%f", p2.getValue()).replaceAll("e", "\\$e");
			// 使用字符串比较器进行比较
			return str2.compareTo(str1); // 注意这里是str2和str1的顺序，实现降序排序
		});
		ArrayList<Pair<String, Double>> pairList = new ArrayList<>(Arrays.asList(pairs));

		int countToReturn = Math.min(pairList.size(), 7);
		// 获取从倒数第7个开始到最后一个的所有元素
		Pair<String, Double>[] lastSevenPairs = new Pair[countToReturn];
		for (int i = 0; i < countToReturn; i++) {
			lastSevenPairs[i] = pairList.get(pairList.size() - countToReturn + i);
		}
		List<String> values = new LinkedList<>();
		for (Pair<String, Double> lastSevenPair : lastSevenPairs) {
			values.add(lastSevenPair.getKey());
		}
		return values;
	}

	/**
	 * 加载数据，并且创建如下域
	 * featureList：特征函数的list
	 * featureCountList:与特征函数一一对应的，特征函数出现的次数
	 * instanceList:样本数据list
	 * labels:类别list
	 *
	 * @param path
	 * @throws IOException
	 */
	public static void loadData(String path) throws IOException {

		BufferedReader br = new BufferedReader(new FileReader(new File(path)));
		String line = br.readLine();
		while (line != null) {
			String[] segs = line.split("\\s");
			String label = segs[0];
			List<String> fieldList = new ArrayList<String>();
			for (int i = 1; i < segs.length; ++i) {
				fieldList.add(segs[i]);
				Feature feature = new Feature(label, segs[i]);
				int index = featureList.indexOf(feature);
				if (index == -1) {
					featureList.add(feature);
					featureCountList.add(1);
				} else {
					featureCountList.set(index, featureCountList.get(index) + 1);
				}
			}
			if (fieldList.size() > C) C = fieldList.size();
			Instance instance = new Instance(label, fieldList);
			instanceList.add(instance);
			if (!labels.contains(label)) labels.add(label);
			line = br.readLine();
		}
	}

	/**
	 * 训练模型
	 *
	 * @param maxIt 最大迭代次数
	 */
	public static void train(int maxIt) {
		int size = featureList.size();
		weight = new double[size];               // 特征权重
		double[] empiricalE = new double[size];   // 经验期望
		double[] modelE = new double[size];       // 模型期望

		for (int i = 0; i < size; ++i) {
			empiricalE[i] = (double) featureCountList.get(i) / instanceList.size();
		}

		double[] lastWeight = new double[weight.length];  // 上次迭代的权重
		for (int i = 0; i < maxIt; ++i) {
			computeModeE(modelE);
			for (int w = 0; w < weight.length; w++) {
				lastWeight[w] = weight[w];
				weight[w] += 1.0 / C * Math.log(empiricalE[w] / modelE[w]);
			}
			if (checkConvergence(lastWeight, weight)) break;
		}
	}

	/**
	 * 预测类别
	 *
	 * @param fieldList
	 * @return
	 */
	public static Pair<String, Double>[] predict(List<String> fieldList) {
		double[] prob = calProb(fieldList);
		Pair<String, Double>[] pairResult = new Pair[prob.length];
		for (int i = 0; i < prob.length; ++i) {
			pairResult[i] = new Pair<String, Double>(labels.get(i), prob[i]);
		}

		return pairResult;
	}

	/**
	 * 检查是否收敛
	 *
	 * @param w1
	 * @param w2
	 * @return 是否收敛
	 */
	public static boolean checkConvergence(double[] w1, double[] w2) {
		for (int i = 0; i < w1.length; ++i) {
			if (Math.abs(w1[i] - w2[i]) >= 0.01)    // 收敛阀值0.01可自行调整
				return false;
		}
		return true;
	}

	/**
	 * 计算模型期望，即在当前的特征函数的权重下，计算特征函数的模型期望值。
	 *
	 * @param modelE 储存空间，应当事先分配好内存（之所以不return一个modelE是为了避免重复分配内存）
	 */
	public static void computeModeE(double[] modelE) {
		Arrays.fill(modelE, 0.0f);
		for (int i = 0; i < instanceList.size(); ++i) {
			List<String> fieldList = instanceList.get(i).fieldList;
			//计算当前样本X对应所有类别的概率
			double[] pro = calProb(fieldList);
			for (int j = 0; j < fieldList.size(); j++) {
				for (int k = 0; k < labels.size(); k++) {
					Feature feature = new Feature(labels.get(k), fieldList.get(j));
					int index = featureList.indexOf(feature);
					if (index != -1) {
						if (modelE.length <= index) {
							continue;
						}
						modelE[index] += pro[k] * (1.0 / instanceList.size());
					}
				}
			}
		}
	}

	/**
	 * 计算p(y|x),此时的x指的是instance里的field
	 *
	 * @param fieldList 实例的特征列表
	 * @return 该实例属于每个类别的概率
	 */
	public static double[] calProb(List<String> fieldList) {
		double[] p = new double[labels.size()];
		double sum = 0;  // 正则化因子，保证概率和为1
		for (int i = 0; i < labels.size(); ++i) {
			double weightSum = 0;
			for (String field : fieldList) {
				Feature feature = new Feature(labels.get(i), field);
				int index = featureList.indexOf(feature);
				if (index != -1)
					weightSum += weight[index];
			}
			p[i] = Math.exp(weightSum);
			sum += p[i];
		}
		for (int i = 0; i < p.length; ++i) {
			p[i] /= sum;
		}
		return p;
	}
}