/* Rules for non-functional failure modes */

internalServerErr(SVC_A, ENDPOINT, TIME, X) :-
  highLatency(SVC_A, ENDPOINT, TIME, X);
  highMemoryUsage(SVC_A, TIME, X);  
  (httpReq(SVC_A, SVC_B, ENDPOINT_B), 
    (
      internalServerErr(SVC_B, ENDPOINT_B, TIME, X)
    ), !
  );
  functionalFailure(SVC_A, ENDPOINT, TIME, X).

highLatency(SVC_A, ENDPOINT_A, TIME, X) :- 
  (trafficSpike(SVC_A, TIME, X), !);
  (downstreamHighLatency(SVC_A, ENDPOINT_A, TIME, X), !);
  (highCpuUsageAlert(SVC_A, TIME, X), !);
  highLatencyInternal(SVC_A, ENDPOINT_A, TIME, X).

downstreamHighLatency(SVC_A, ENDPOINT_A, TIME, X) :- 
  httpReq(SVC_A, SVC_B, ENDPOINT_B),
  ( 
    (networkFailure(SVC_B, TIME, X), !);
    (processTerminated(SVC_B, TIME, X), !);
    highLatency(SVC_B, ENDPOINT_B, TIME, X)
  ).
 

highCpuUsage(SVC, TIME, X) :-
  highMemoryUsage(SVC, TIME, X);
  trafficSpike(SVC, TIME, X).


/* Terminal Rules */
internalServerErrAlert(SVC, ENDPOINT, TIME, X) :- fail.
trafficSpike(SVC, TIME, X) :- fail.
networkFailure(SVC, TIME, X) :- fail.
processTerminated(SVC, TIME, X) :- fail.
highCpuUsageAlert(SVC, TIME, X) :- fail.
highMemoryUsage(SVC, TIME, X) :- fail.
highLatencyInternal(SVC, TIME, X) :- fail.
internalServerErrAlert(SVC, ENDPOINT, TIME, X) :- fail.

functionalFailure(SVC, ENDPOINT, 'Functional Failure'). 

/* --------------------------------------------------------------------------------------*/
/* Facts for testing */
httpReq(a, b, '/test').
highLatency(b, '/test', 8, "high latency in B").
