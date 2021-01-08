package memory.memory;

import memory.DiskInterface;
import memory.MemoryInterface;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @Author: pkun
 * @CreateTime: 2020-11-27 21:18
 */
public class ProxyDisk implements InvocationHandler {
    private DiskInterface proxied;

    ProxyDisk(DiskInterface proxied) {
        this.proxied = proxied;
    }

    @Override
    public Object
    invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        try {
            Thread.sleep(150);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return method.invoke(proxied, args);
    }
}
