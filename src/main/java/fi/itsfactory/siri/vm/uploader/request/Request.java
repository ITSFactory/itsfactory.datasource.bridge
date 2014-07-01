package fi.itsfactory.siri.vm.uploader.request;

import java.util.List;
import java.util.Map;

import fi.itsfactory.siri.vm.uploader.listener.ResponseListener;

public interface Request {
    public String getAddress();

    public Map<String, String> getParameters();

    public String getPayload();

    public long getInterval();

    public List<ResponseListener> getListeners();

    public void setListeners(List<ResponseListener> listeners);
}
