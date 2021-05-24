/* Rules for non-functional failure modes */

internalServerErr(SVC_A, ENDPOINT, TIME, X) :-
  highLatency(SVC_A, ENDPOINT, TIME, X);  
  functionalFailure(SVC_A, ENDPOINT, X). 

highLatency(SVC_A, ENDPOINT_A, TIME, X) :- 
  highCpuUsage(SVC_A, TIME, X);
  highLatencyInternal(SVC_A, ENDPOINT, TIME, X);
  httpReq(SVC_A, SVC_B, ENDPOINT_B), 
    (
      highLatency(SVC_B, ENDPOINT_B, TIME, X)
    ).



/* Terminal Rules */
trafficSpike(SVC, TIME, X) :- fail.
processTerminated(SVC, TIME, X) :- fail.
highCpuUsage(SVC, TIME, X) :- fail.
highMemoryUsage(SVC, TIME, X) :- fail.
highLatencyInternal(SVC, TIME, X) :- fail.
internalServerErrAlert(SVC, ENDPOINT, TIME, X) :- fail.

functionalFailure(SVC, ENDPOINT, 'Functional Failure'). 

/* --------------------------------------------------------------------------------------*/
/* Facts for testing */
httpReq(a, b, '/test').
highLatency(b, '/test', 8, "high latency in B").
