package org.karnak.backend.service.dicom;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.karnak.backend.model.dicom.ConfigNode;
import org.karnak.backend.service.thread.DicomEchoThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DicomEchoService {

	@Autowired
	public DicomEchoService() {
	}

	public String dicomEcho(List<ConfigNode> nodes) throws InterruptedException, ExecutionException {
		StringBuilder result = new StringBuilder();

		List<Future<String>> threadsResult = createThreadsResult(nodes);
		for (Future<String> threadResult : threadsResult) {
			result.append(threadResult.get());
		}

		return result.toString();
	}

	private List<Future<String>> createThreadsResult(List<ConfigNode> nodes) throws InterruptedException {
		List<Future<String>> threadResult = null;
		try {
			ExecutorService executorService = Executors.newFixedThreadPool(nodes.size());

			List<DicomEchoThread> threads = new ArrayList<>();

			for (ConfigNode node : nodes) {
				DicomEchoThread dicomEchoThread = new DicomEchoThread(node);
				threads.add(dicomEchoThread);
			}

			threadResult = executorService.invokeAll(threads);

			return threadResult;
		} catch (InterruptedException e) {
			throw e;
		}
	}

}
