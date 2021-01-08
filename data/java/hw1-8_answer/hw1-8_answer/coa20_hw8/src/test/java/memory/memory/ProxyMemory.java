package memory.memory;

import memory.MemoryInterface;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @CreateTime: 2020-11-07 14:23
 */
public class ProxyMemory implements InvocationHandler {
    private MemoryInterface proxied;

    ProxyMemory(MemoryInterface proxied) {
        this.proxied = proxied;
    }

    @Override
    public Object
    invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        try {
            Thread.sleep(15);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return method.invoke(proxied, args);
    }
}
