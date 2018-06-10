package io.github.ningyu.fastdfs.protocol;

import io.github.ningyu.fastdfs.utils.FastDfsParamMapperUtils;
import io.github.ningyu.fastdfs.utils.ReflectionsUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * FastDFS请求响应 基类
 * @author ningyu
 * @date 2017年5月17日 下午4:46:03
 */
public abstract class BaseResponse<T> {
    /**
     * 报文头
     */
    private ProtocolHead head;

    /**
     * 返回值泛型类型
     */
    private final Class<T> genericType;

    /**
     * 构造函数
     */
    public BaseResponse() {
        this.genericType = ReflectionsUtils.getClassGenricType(getClass());
    }

    /**
     * 获取报文长度
     */
    protected long getContentLength() {
        return head.getContentLength();
    }

    /**
     * 解析反馈结果 请求头报文使用 {@link ProtocolHead#createFromInputStream(InputStream)} 解析
     */
    T decode(ProtocolHead head, InputStream in, Charset charset) throws IOException {
        this.head = head;
        return decodeContent(in, charset);
    }

    /**
     * 解析反馈内容
     */
    public T decodeContent(InputStream in, Charset charset) throws IOException {
        // 如果有内容
        if (getContentLength() > 0) {
            byte[] bytes = new byte[(int) getContentLength()];
            int contentSize = in.read(bytes);
            // 获取数据
            if (contentSize != getContentLength()) {
                throw new IOException("读取到的数据长度与协议长度不符");
            }
            return FastDfsParamMapperUtils.map(bytes, genericType, charset);
        }
        return null;
    }
}