package eniac;

import java.io.InputStream;

public interface ResourceProvider {

    public InputStream openStream(String file);
}
