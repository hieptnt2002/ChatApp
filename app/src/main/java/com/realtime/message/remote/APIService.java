package com.realtime.message.remote;

import com.realtime.message.notification.MyResponse;
import com.realtime.message.notification.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAANWjCCb4:APA91bFiExDkoxbC5KSrtKeHWy-DV6EeCCDvdCyTQAzBvIII6oIVOKmQ4yS32R0WD2rycslV0enTomzZ0QeHk-kt4hAmzxEvJbWE257K_KN08eTEia24-f29oeeQBYGhI356M8gGUnFl"
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
