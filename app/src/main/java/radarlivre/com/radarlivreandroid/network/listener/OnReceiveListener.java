package radarlivre.com.radarlivreandroid.network.listener;

public interface OnReceiveListener<T> {

    void onReceivedSuccessfully(T object);
    void onReceivedFailed();

}