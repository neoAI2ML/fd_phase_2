package tomketao.fd_phase_2.training;

import java.io.IOException;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import tomketao.fd_phase_2.data.FeatureKnowledge;
import tomketao.fd_phase_2.data.TrainingSetting;
import tomketao.fd_phase_2.training.ScamTrainingProcess;

public class ScamTrainingProcessTest {
	private FeatureKnowledge knowledge = new FeatureKnowledge();
	private TrainingSetting trainingSetting;

	@BeforeClass
	public void beforeClass() {
		trainingSetting = TrainingSetting.loadFromFile("conf/fd-config.json");
		knowledge = new FeatureKnowledge();

	}

	@Test
	public void learningProcess() {
		try {
			ScamTrainingProcess.learningProcess(knowledge, trainingSetting, "data/scam-training");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
