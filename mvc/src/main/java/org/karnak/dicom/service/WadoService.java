package org.karnak.dicom.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.karnak.dicom.model.WadoNode;
import org.karnak.dicom.thread.CheckWadoThread;

public class WadoService {
	
	public String checkWado(List<WadoNode> nodes) throws InterruptedException, ExecutionException {
		StringBuilder result = new StringBuilder();

		List<Future<String>> threadsResult = createThreadsResult(nodes);
		for (Future<String> threadResult : threadsResult) {
			result.append(threadResult.get());
		}
		
		return result.toString();
	}
	
	private List<Future<String>> createThreadsResult(List<WadoNode> nodes) throws InterruptedException {
		List<Future<String>> threadResult = null;
		try {
			ExecutorService executorService = Executors.newFixedThreadPool(nodes.size());
			
			List<CheckWadoThread> threads = new ArrayList<>();

			for (WadoNode node : nodes) {
				CheckWadoThread checkWadoThread = new CheckWadoThread(node);
				threads.add(checkWadoThread);
			}
			
			threadResult = executorService.invokeAll(threads);
			
			return threadResult;
		} catch (InterruptedException e) {
			throw e;
		}
	}

}
