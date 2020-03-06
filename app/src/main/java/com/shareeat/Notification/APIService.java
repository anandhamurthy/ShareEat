package com.shareeat.Notification;

import com.shareeat.Notification.MyResponse;
import com.shareeat.Notification.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {


    @Headers(
            {
                    "Content-type:application/json",
                    "Authorization:key=AAAAw7vIgmo:APA91bHvunPgbBWamb4CrPoRdQ5cLZF8OVC3vT0V8RFG3aQIaCaqgrjI7sliNyoXZffuKxeJ3S2D-JaJW9i6KkLkH27q0ztOOA4vqOb2julx-KhKPokRAWGIyHjXyp1yomeWLMO0MFot"

            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);

}
