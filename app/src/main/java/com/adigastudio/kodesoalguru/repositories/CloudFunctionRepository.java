package com.adigastudio.kodesoalguru.repositories;

import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;

public class CloudFunctionRepository {

    private FirebaseFunctions functions;

    private void getFunctionsInstance(){
        if(functions == null){
            functions = FirebaseFunctions.getInstance();
        }
    }

    public Task<Long> getServerTime() {
        getFunctionsInstance();
        return functions
                .getHttpsCallable("getServerTime")
                .call()
                .continueWith(task -> (Long) task.getResult().getData());
    }
}
