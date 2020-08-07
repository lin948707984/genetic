package schedulingAlgorithm.geneticAlgorithm;

import java.util.*;

/**
 * 遗传算法核心逻辑
 * @author lin
 * @Date 2020.3.6
 */
public class GeneticAlgorithm {

	/**
	 * 初始化遗传算法
	 * @param geneticModel
	 * @param jobNumber
	 * @param machineNumber
	 */
	public GeneticAlgorithm(GeneticModel geneticModel, int jobNumber, int machineNumber) {
		// 初始化工件数量
		geneticModel.setJobNumber(jobNumber);
		// 初始化设备数量
		geneticModel.setMachineNumber(machineNumber);
		// 初始化设备矩阵
		for (int[] matrix : geneticModel.machineMatrix) {
			Arrays.fill(matrix, -1);
		}
		// 初始化工序矩阵
		for (int[] process : geneticModel.processMatrix) {
			Arrays.fill(process, -1);
		}
	}

	/**
	 * 遗传算法主逻辑
	 * 已知一个工件的一个工序只能在一个设备上进行加工，且工件的加工顺序不能变化
	 * 一个设备同一时间只能加工一个工件
	 * （现实生产中可能出现一个设备可以同时加工两个工件的现象，考虑后续在设备上加上一个最大加工数量，用于调整一个设备加工两个工件或者虚拟设备加工多个工件的现象）
	 * 1.用随机方法，先制定出一个字符串，用来表示工件的加工顺序，定为染色体
	 *   例：[1，2，3，2，3，1，2] 工件第一次出现为加工第一道工序，第二次出现为加工第二道工序
	 * 2.制定多组染色体存入一个集群
	 * 3.挑选集群中的一部分染色体，计算这部分染色体中的最优时间，挑选两组进行遗传繁衍（交叉算子）或者进行变异（变异算子）
	 *   若计算出最优时间为最优解则终止遗传，输出结果
	 * 4.挑选集群中的一部分染色体，计算这部分染色体中的最坏时间进行淘汰，优化存货集群（优化算法，暂未实现）
	 * 5.将繁衍后的染色体加入到集群，重复3至多代
	 * 6.获取最优计算时间
	 * @param geneticModel
	 * @param job
	 * @param minTime
	 * @return
	 */
	public Result run(GeneticModel geneticModel, List<List<Integer[]>> job, int minTime) {
		// 获取工件数量
		int jobSize = job.size();

		// 循环每个工件，取工件中工序最多的工序作为工序最大值使用
		// 设定设备矩阵和设备用时时间矩阵的
		for (int i = 0; i < jobSize; i++) {
			geneticModel.chromosomeSize += job.get(i).size();
			geneticModel.processNumber = Math.max(geneticModel.processNumber, job.get(i).size());
			for (int j = 0; j < job.get(i).size(); j++) {
				geneticModel.machineMatrix[i][j] = job.get(i).get(j)[0];
				geneticModel.timeMatrix[i][j] = job.get(i).get(j)[1];
			}
		}
		// 循环每个工件，设置每个工件的工序加工所需要的设备的耗时
		for (int i = 0; i < jobSize; i++) {
			for (int j = 0; j < geneticModel.processNumber; j++) {
				if (geneticModel.machineMatrix[i][j] != -1) {
					geneticModel.processMatrix[i][geneticModel.machineMatrix[i][j]] = j;
				}
			}
		}
		// 初始化基因种群
		this.initialPopulation(geneticModel, job);

		// 遍历基因种群
		for (int i = 0; i < geneticModel.populationNumber; i++) {
			// 获取随机数，按照设定值，进行变异或者遗传
			double p = (double) geneticModel.random.nextInt(100) / 100.0;
			if (p < geneticModel.mutationProbability) {
				// 随机选择一个基因种群中的染色体
				int index = geneticModel.random.nextInt(geneticModel.geneSet.size());
				int k = 0;
				for (Gene gene : geneticModel.geneSet) {
					if (k == index) {
						// 激动人心的基因突变开始了
						this.mutationGene(geneticModel, gene);
						break;
					}
					k++;
				}
			} else {
				// 挑选两个集群的最优个体进行繁殖下一代
				Gene g1 = this.selectGene(geneticModel), g2 = this.selectGene(geneticModel);
				// 如果最优个体中出现最优解，则结束遗传
				if (this.calculateFitness(geneticModel, g1).fulfillTime == minTime || this.calculateFitness(geneticModel, g2).fulfillTime == minTime) {
					break;
				}
				// 两个最优个体相互繁衍下一代，并加入到基因群众
				Gene child1 = this.crossGene(geneticModel, g1, g2), child2 = this.crossGene(geneticModel, g2, g1);
				geneticModel.geneSet.add(child1);
				geneticModel.geneSet.add(child2);
			}
		}
		// 遍历所有集群，寻找最优解
		Gene bestGene = new Gene(0xffffff);
		for (Gene gene : geneticModel.geneSet) {
			if (bestGene.fitness > gene.fitness) {
				bestGene = gene;
			}
		}
		// 计算适应度
		return this.calculateFitness(geneticModel, bestGene);
	}


	/**
	 * 初始化种群
	 * @param job
	 */
	public void initialPopulation(GeneticModel geneticModel, List<List<Integer[]>> job) {
		for (int i = 0; i < geneticModel.populationNumber; i++) {
			Gene g = new Gene();
			int jobSizeMax = 0;
			// 获取工件的最大工序数量，最大工序数x工件数，做成染色体的基本长度
			for (List<Integer[]> jopSmall : job) {
				if (jopSmall.size() > jobSizeMax) {
					jobSizeMax = jopSmall.size();
				}
			}
			int size = geneticModel.getJobNumber() * jobSizeMax;
			List<Integer> indexList = makeList(size);
			// 初始化染色体长度
			Integer[] chromosome = new Integer[size];
			// 初始化染色体
			Arrays.fill(chromosome, -1);
			// 遍历工件和工序，将工序随机插入到染色体中
			for (int j = 0; j < geneticModel.getJobNumber(); j++) {
				for (int k = 0; k < job.get(j).size(); k++) {
					int index = geneticModel.random.nextInt(indexList.size());
					int val = indexList.remove(index);
					chromosome[val] = j;
				}
			}
			// 清除掉没有被赋值的元素
			g.chromosome = filterArray(chromosome, -1);
			// 计算适应度
			g.fitness = calculateFitness(geneticModel, g).fulfillTime;
			// 将染色体插入到种群中
			geneticModel.geneSet.add(g);
		}
	}

	/**
	 * 计算适应度
	 * @param geneticModel
	 * @param gene
	 * @return
	 */
	public Result calculateFitness(GeneticModel geneticModel, Gene gene) {
		Result result = new Result();
		Set<String> machineSet = new HashSet<>();
		for (int i = 0; i < gene.chromosome.length; i++) {
			// 获取工件编号
			int jobId = gene.chromosome[i];
			// 获取工件的工序
			int processId = result.processIds[jobId];
			// 根据工件和工序或取设备编号
			int machineId = geneticModel.machineMatrix[jobId][processId];
			int equipmentTime = 0;
			if(!machineSet.contains(""+machineId)){
				equipmentTime = geneticModel.equipmentList.get(machineId)[0];
			}
			machineSet.add( machineId+"");
			// 获取加工需要的时间
			int time = geneticModel.timeMatrix[jobId][processId];
			result.processIds[jobId] += 1;
			// 当工序为第一道工序的时候，开始时间为设备时间，否则取设备时间和上一道工序使用该设备的结束时间的比较的最大值
			result.startTime[jobId][processId] = processId == 0 ? result.machineWorkTime[machineId]+equipmentTime : Math.max(result.endTime[jobId][processId - 1], result.machineWorkTime[machineId])+equipmentTime;
			// 虚拟设备时，不去更新设备时间，直接加工时，更新工序结束时间
			// 设备时间更新为工序开始时间加上工序的加工时间
			System.out.print( processId +" ");
			System.out.print(time/geneticModel.equipmentList.get(machineId)[1] * 24);
			System.out.print(" ");
			System.out.print((result.startTime[jobId][processId]%geneticModel.equipmentList.get(machineId)[1] + time%geneticModel.equipmentList.get(machineId)[1])/geneticModel.equipmentList.get(machineId)[1]*24);
			System.out.println((time/geneticModel.equipmentList.get(machineId)[1] * 24)
					+((result.startTime[jobId][processId]%geneticModel.equipmentList.get(machineId)[1] + time%geneticModel.equipmentList.get(machineId)[1])/geneticModel.equipmentList.get(machineId)[1]*24)
					+((result.startTime[jobId][processId] + time%geneticModel.equipmentList.get(machineId)[1])%geneticModel.equipmentList.get(machineId)[1])
					+ (result.startTime[jobId][processId] - result.startTime[jobId][processId]%geneticModel.equipmentList.get(machineId)[1]));

  			result.machineWorkTime[machineId] =
					(time/geneticModel.equipmentList.get(machineId)[1] * 24)
							+((result.startTime[jobId][processId]%geneticModel.equipmentList.get(machineId)[1] + time%geneticModel.equipmentList.get(machineId)[1])/geneticModel.equipmentList.get(machineId)[1]*24)
							+((result.startTime[jobId][processId] + time%geneticModel.equipmentList.get(machineId)[1])%geneticModel.equipmentList.get(machineId)[1])
							+ (result.startTime[jobId][processId] - result.startTime[jobId][processId]%geneticModel.equipmentList.get(machineId)[1]);
			// 工件的工序结束时间
			result.endTime[jobId][processId] = result.machineWorkTime[machineId];
			// 最大时间为适应度时间
			result.fulfillTime = Math.max(result.fulfillTime, result.machineWorkTime[machineId]);
		}
		return result;
	}

	/**
	 * 交叉算子
	 * 对于一对染色体g1, g2，首先随机产生一个起始位置start和终止位置end
	 * 并由从g1的染色体序列从start到end的序列中产生一个子代
	 * @param geneticModel
	 * @param g1
	 * @param g2
	 * @return
	 */
	private Gene crossGene(GeneticModel geneticModel, Gene g1, Gene g2) {
		List<Integer> indexList = makeList(geneticModel.chromosomeSize);
		// 随机产生两个位置
		int p1 = indexList.remove(geneticModel.random.nextInt(indexList.size()));
		int p2 = indexList.remove(geneticModel.random.nextInt(indexList.size()));
		// 设定开始位置和结束位置
		int start = Math.min(p1, p2);
		int end = Math.max(p1, p2);

		// 获取染色体原型
		List<Integer> proto = subArray(g1.chromosome, start, end + 1);
		List<Integer> newList = new ArrayList<>();
		// 复制2号染色体
		for (Integer val : g2.chromosome) {
			newList.add(val);
		}
		// 将原型工件在2号染色体中删除
		for (Integer val : proto) {
			for (int i = 0; i < newList.size(); i++) {
				if (val.equals(newList.get(i))) {
					newList.remove(i);
					break;
				}
			}
		}

		// 将原型工件，插入到2号染色体中
		Gene child = new Gene();
		proto.addAll(newList.subList(start, newList.size()));
		List<Integer> childGene = newList.subList(0, start);
		childGene.addAll(proto);
		// 整理新的染色体
		child.chromosome = childGene.toArray(new Integer[0]);
		child.fitness = (double) calculateFitness(geneticModel, child).fulfillTime;
		return child;
	}

	/**
	 * 突变算子
	 * 变异的作用主要是使算法能跳出局部最优解，因此不同的变异方式对算法能否求得全局最优解有很大的影响。
	 * 使用位置变异法作为变异算子，即从染色体中随机产生两个位置并交换这两个位置的值
	 * 如果遗传次数过多但并未出现最优解，可以调整突变概率和突变算法
	 * @param geneticModel
	 * @param gene
	 * @return
	 */
	public Gene mutationGene(GeneticModel geneticModel, Gene gene) {
		List<Integer> indexList = makeList(geneticModel.chromosomeSize);
		for (int i = 0; i < geneticModel.mutationGeneNum; i++) {
			// 随机选择位置并进行交换
			int a = indexList.remove(geneticModel.random.nextInt(indexList.size()));
			int b = indexList.remove(geneticModel.random.nextInt(indexList.size()));
			int t = gene.chromosome[a];
			gene.chromosome[a] = gene.chromosome[b];
			gene.chromosome[b] = t;
		}
		// 计算适应度
		gene.fitness = this.calculateFitness(geneticModel, gene).fulfillTime;
		return gene;
	}

	/**
	 * 选择最优个体，用于遗传
	 * 在基因种群中选择一部分群体，个体竞争，选择最好的个体
	 * @param geneticModel
	 * @return
	 */
	public Gene selectGene(GeneticModel geneticModel) {
		List<Integer> indexList = makeList(geneticModel.geneSet.size());
		Map<Integer, Boolean> map = new HashMap<>();
		for (int i = 0; i < geneticModel.selectGeneNum; i++) {
			map.put(indexList.remove(geneticModel.random.nextInt(indexList.size())), true);
		}
		Gene bestGene = new Gene(0xfffff);
		int i = 0;
		// 遍历选择集合染色体的适应度，选取最好的
		for (Gene gene : geneticModel.geneSet) {
			if (map.containsKey(i)) {
				if (bestGene.fitness > gene.fitness) {
					bestGene = gene;
				}
			}
			i++;
		}
		return bestGene;
	}

	/**
	 * 计算当前工序和设备生产关系的最优解
	 * 最优解算法为，计算单个设备的满负荷工作时间，满负荷的最大值就是理论上的最优解的总耗时时间
	 * 公式：argMax i (SUM(machineTime[i][j]) (i=1,2,3,....,m) (j=1,2,3,....,processNumber)) 大概这么描述吧
	 * @param geneticModel
	 * @return
	 */
	public static int optimalSolution(GeneticModel geneticModel, List<List<Integer[]>> job) {
		// 获取最优解的设备时长
		int minTime = 0;
		// 循环所有设备
		for (int i = 0; i < geneticModel.getMachineNumber(); i++) {
			int machineMinTime = 0;
			// 遍历生产基本信息，获取对应设备编号的设备加工总时长
			for (List<Integer[]> list : job) {
				for (Integer[] listInt : list) {
					machineMinTime = machineMinTime + (listInt[0] == i ? listInt[1] : 0);
				}
			}
			// 获取最大时长
			minTime = minTime > machineMinTime ? minTime : machineMinTime;
		}
		return minTime;
	}

	/**
	 * 清除掉没有被赋值的指定元素
	 * @param arr
	 * @param filterVal
	 * @return
	 */
	private Integer[] filterArray(Integer[] arr, int filterVal) {
		List<Integer> result = new ArrayList<>();
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] != filterVal) {
				result.add(arr[i]);
			}
		}
		return result.toArray(new Integer[0]);
	}

	/**
	 * 创建list
	 * @param n
	 * @return
	 */
	private List<Integer> makeList(int n) {
		List<Integer> result = new ArrayList<>();
		for (int i = 0; i < n; i++) {
			result.add(i);
		}
		return result;
	}

	/**
	 * 截取目标数组
	 * @param arr
	 * @param start
	 * @param end
	 * @return
	 */
	public List<Integer> subArray(Integer[] arr, int start, int end) {
		List<Integer> list = new ArrayList<>();
		for (int i = start; i < end; i++) {
			list.add(arr[i]);
		}
		return list;
	}
}
