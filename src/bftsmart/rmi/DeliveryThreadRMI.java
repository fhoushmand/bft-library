///**
//Copyright (c) 2007-2013 Alysson Bessani, Eduardo Alchieri, Paulo Sousa, and the authors indicated in the @author tags
//
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at
//
//http://www.apache.org/licenses/LICENSE-2.0
//
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.
//*/
//package bftsmart.rmi;
//
//import bftsmart.reconfiguration.ServerViewController;
//import bftsmart.statemanagement.ApplicationState;
//import bftsmart.tom.MessageContext;
//import bftsmart.tom.ServiceReplica;
//import bftsmart.tom.core.messages.RTMessage;
//import bftsmart.tom.core.messages.RTMessageType;
//import bftsmart.tom.leaderchange.CertifiedDecision;
//import bftsmart.tom.server.Recoverable;
//import bftsmart.tom.util.BatchReader;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.ArrayList;
//import java.util.concurrent.LinkedBlockingQueue;
//import java.util.concurrent.locks.Condition;
//import java.util.concurrent.locks.Lock;
//import java.util.concurrent.locks.ReentrantLock;
//
///**
// * This class implements a thread which will deliver totally ordered requests to
// * the application
// *
// */
//public final class DeliveryThreadRMI extends Thread {
//
//	private Logger logger = LoggerFactory.getLogger(this.getClass());
//
//	private boolean doWork = true;
//	private int lastReconfig = -2;
//	private final TOMLayerBroadcast tomLayerBroadcast; // TOM layer
//	private final ServiceReplicaRMI receiver; // Object that receives requests from clients
//	private final Recoverable recoverer; // Object that uses state transfer
//	private final ServerViewController controller;
//
//	/**
//	 * Creates a new instance of DeliveryThread
//	 *
//	 * @param tomLayer TOM layer
//	 * @param receiver Object that receives requests from clients
//	 */
//	public DeliveryThreadRMI(TOMLayerBroadcast tomLayer, ServiceReplicaRMI receiver, Recoverable recoverer,
//                             ServerViewController controller) {
//		super("Delivery Thread");
//
//		this.tomLayerBroadcast = tomLayer;
//		this.receiver = receiver;
//		this.recoverer = recoverer;
//		// ******* EDUARDO BEGIN **************//
//		this.controller = controller;
//		// ******* EDUARDO END **************//
//	}
//
//	public Recoverable getRecoverer() {
//		return recoverer;
//	}
//
//
//	/** THIS IS JOAO'S CODE, TO HANDLE STATE TRANSFER */
//	private ReentrantLock deliverLock = new ReentrantLock();
//	private Condition canDeliver = deliverLock.newCondition();
//
//	public void deliverLock() {
//		deliverLock.lock();
//	}
//
//	public void deliverUnlock() {
//		deliverLock.unlock();
//	}
//
//	public void canDeliver() {
//		canDeliver.signalAll();
//	}
//
//	public void update(ApplicationState state) {
//		logger.info("All finished up to");
//	}
//
//	/**
//	 * This is the code for the thread. It delivers decisions to the TOM request
//	 * receiver object (which is the application)
//	 */
//	@Override
//	public void run() {
//		boolean init = true;
//		while (doWork) {
//			/** THIS IS JOAO'S CODE, TO HANDLE STATE TRANSFER */
//			deliverLock();
//			while (tomLayerBroadcast.isRetrievingState()) {
//				logger.info("Retrieving State");
//				canDeliver.awaitUninterruptibly();
//
//				// if (tomLayer.getLastExec() == -1)
//				if (init) {
//					logger.info(
//							"\n\t\t###################################"
//					      + "\n\t\t    Ready to process operations    "
//						  + "\n\t\t###################################");
//					init = false;
//				}
//			}
//			try {
//				if (!doWork)
//					break;
//
//				if (decisions.size() > 0) {
//					RTMessage[][] requests = new RTMessage[decisions.size()][];
//					int[] consensusIds = new int[requests.length];
//					int[] leadersIds = new int[requests.length];
//					int[] regenciesIds = new int[requests.length];
//					CertifiedDecision[] cDecs;
//					cDecs = new CertifiedDecision[requests.length];
//					int count = 0;
//					for (Decision d : decisions) {
//						requests[count] = extractMessagesFromDecision(d);
//						consensusIds[count] = d.getConsensusId();
//						leadersIds[count] = d.getLeader();
//						regenciesIds[count] = d.getRegency();
//
//						CertifiedDecision cDec = new CertifiedDecision(this.controller.getStaticConf().getProcessId(),
//								d.getConsensusId(), d.getValue(), d.getDecisionEpoch().proof);
//						cDecs[count] = cDec;
//
//						// cons.firstMessageProposed contains the performance counters
//						if (requests[count][0].equals(d.firstMessageProposed)) {
//							long time = requests[count][0].timestamp;
//							long seed = requests[count][0].seed;
//							int numOfNonces = requests[count][0].numOfNonces;
//							requests[count][0] = d.firstMessageProposed;
//							requests[count][0].timestamp = time;
//							requests[count][0].seed = seed;
//							requests[count][0].numOfNonces = numOfNonces;
//						}
//
//						count++;
//					}
//
//					if (requests != null && requests.length > 0) {
//						deliverMessages(consensusIds, regenciesIds, leadersIds, cDecs, requests);
//					}
//				}
//			} catch (Exception e) {
//				logger.error("Error while processing decision", e);
//			}
//
//			/** THIS IS JOAO'S CODE, TO HANDLE STATE TRANSFER */
//			deliverUnlock();
//			/******************************************************************/
//		}
//		logger.info("DeliveryThread stopped.");
//
//	}
//
//	protected void deliverUnordered(RTMessage request) {
//
//		MessageContext msgCtx = new MessageContext(request.getSender(), request.getViewID(), request.getReqType(),
//				request.getSession(), request.getSequence(), request.getOperationId(), request.getReplyServer(),
//				request.serializedMessageSignature, System.currentTimeMillis(), 0, 0, -1, -1, -1, null, null,
//				false); // Since the request is unordered,
//						// there is no consensus info to pass
//
//		msgCtx.readOnly = true;
//		receiver.receiveReadonlyMessage(request, msgCtx);
//	}
//
//	private void deliverMessages(int consId[], int regencies[], int leaders[], CertifiedDecision[] cDecs,
//			RTMessage[][] requests) {
//		receiver.receiveMessages(consId, regencies, leaders, cDecs, requests);
//	}
//
//	private void processReconfigMessages(int consId) {
//		byte[] response = controller.executeUpdates(consId);
//		RTMessage[] dests = controller.clearUpdates();
//
//		if (controller.getCurrentView().isMember(receiver.getId())) {
//			for (int i = 0; i < dests.length; i++) {
//				tomLayerBroadcast.getCommunication().send(new int[] { dests[i].getSender() },
//						new RTMessage(controller.getStaticConf().getProcessId(), dests[i].getSession(),
//								dests[i].getSequence(), dests[i].getOperationId(), response,
//								controller.getCurrentViewId(), RTMessageType.RECONFIG));
//			}
//
//			tomLayerBroadcast.getCommunication().updateServersConnections();
//		} else {
//			receiver.restart();
//		}
//	}
//
//	public void shutdown() {
//		this.doWork = false;
//		logger.info("Shutting down delivery thread");
//	}
//}
