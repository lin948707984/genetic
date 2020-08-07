package schedulingAlgorithm.geneticAlgorithm;

import java.util.*;

/**
 * 亚明生产车间调度算法实例
 * @author lin
 * @Date 2020.3.6
 */
public class Run {
	static public void main(String[] args) {
		System.out.println("亚明生产车间调度算法开始执行！");
		// 程序运行开始时间
		Date startTime = new Date();
		// 创建遗传集合结构体
		GeneticModel geneticModel = new GeneticModel();
		// 设置生产基本信息
		// 每行为一个工件
		// 每个list的内容为工序，[0]为设备编号， [1]为加工耗时
		// ps:生产基本信息为均衡算法计算之后的生产信息，均衡算法用于计算一个工件有多个设备可以加工的情况
		List<List<Integer[]>> job = Arrays.asList(
				Arrays.asList(new Integer[]{2, 8}, new Integer[]{0, 8}, new Integer[]{3, 4}, new Integer[]{2, 24}, new Integer[]{3, 6}),
				Arrays.asList(new Integer[]{0, 4}, new Integer[]{3, 5}, new Integer[]{3, 3}, new Integer[]{2, 4}),
				Arrays.asList(new Integer[]{3, 3}, new Integer[]{3, 7}, new Integer[]{0, 15}, new Integer[]{1, 20}, new Integer[]{0, 8}),
				Arrays.asList(new Integer[]{1, 7}, new Integer[]{2, 6}, new Integer[]{3, 8}, new Integer[]{0, 1}, new Integer[]{3, 16}, new Integer[]{2, 3}),
				Arrays.asList(new Integer[]{3, 10}, new Integer[]{1, 4}, new Integer[]{2, 8}, new Integer[]{3, 4}, new Integer[]{0, 12}, new Integer[]{2, 6}, new Integer[]{3, 1}),
				Arrays.asList(new Integer[]{0, 1}, new Integer[]{1, 4}, new Integer[]{0, 7}, new Integer[]{2, 3}, new Integer[]{3, 5}, new Integer[]{0, 2}, new Integer[]{2, 5}, new Integer[]{0, 8})
		);
		Map<Integer,Integer[]> equipmentList =  new HashMap<>();
		equipmentList.put(0, new Integer[]{0, 24});
		equipmentList.put(1, new Integer[]{0, 24});
		equipmentList.put(2, new Integer[]{0, 24});
		equipmentList.put(3, new Integer[]{0, 24});

		//初始化设备的工作时长和初始值
		geneticModel.equipmentList.putAll(equipmentList);
		// 设定初始设备数量和工件数量
		int n = 6, m = 4;
		// 初始化遗传算法
		GeneticAlgorithm gene = new GeneticAlgorithm(geneticModel, n, m);
		// 计算当前工序和设备生产关系的最优解
		int minTime = GeneticAlgorithm.optimalSolution(geneticModel, job);
		// 输出最优解
		System.out.println("最优解总耗时：" + minTime);
		System.out.println("目前遗传代数：" + geneticModel.populationNumber);
		// 开始计算
		Result result = gene.run(geneticModel, job, minTime);
		// 获取程序计算出来的最优解
		int[][] machineMatrix = geneticModel.machineMatrix;
		// 输出程序计算出来的最优解
		System.out.println("计算最优总耗时："  + result.fulfillTime);

		// 输出结果，结果格式自行调整
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < geneticModel.processNumber; j++) {
				if (machineMatrix[i][j] != -1) {
					System.out.println(String.format("工件: %d, 工序: %d, 设备: %d, 开始时间: %d, 结束时间: %d",
							i + 1, j + 1, machineMatrix[i][j] + 1, result.startTime[i][j], result.endTime[i][j]));
				}
			}
		}
		System.out.println("亚明生产车间调度算法结束！");
		System.out.println("程序用时：" + (new Date().getTime() - startTime.getTime()) / 1000);
	}
}
