package schedulingAlgorithm.geneticAlgorithm;

import java.util.*;

/**
 * 遗传结构体
 * @author lin
 * @Date 2020.3.6
 */
public class GeneticModel {

	// 遗传代数
	public final int populationNumber = 10000;
	// 基因突变几率、遗传几率为1-突变几率
	public final double mutationProbability = 0.05;
	// 优秀个体选择群体数量
	public final int selectGeneNum = 100;
	// 突变个体选择群体数量
	public final int mutationGeneNum = 10;
	// 工件数量
	private int jobNumber;
	// 设备数量
	private int machineNumber;
	// 每个工件的工序数量
	public int processNumber;
	// 遗传染色体长度
	public int chromosomeSize;
	// 设备使用集合矩阵
	public int[][] machineMatrix = new int[1024][1024];
	// 耗时集合矩阵
	public int[][] timeMatrix = new int[1024][1024];
	// 工序集合矩阵
	public int[][] processMatrix = new int[1024][1024];
	//设备列表
	public Map<Integer,Integer[]> equipmentList = new HashMap<>();
	// 染色体种群
	public Set<Gene> geneSet = new HashSet<>();
	// 随机数
	public Random random = new Random();

	public int getJobNumber() {
		return jobNumber;
	}

	public void setJobNumber(int jobNumber) {
		this.jobNumber = jobNumber;
	}

	public int getMachineNumber() {
		return machineNumber;
	}

	public void setMachineNumber(int machineNumber) {
		this.machineNumber = machineNumber;
	}




}
