package com.rockyshen.core.server.tcp;

import com.rockyshen.core.protocal.ProtocolConstant;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.parsetools.RecordParser;

import javax.xml.ws.handler.MessageContext;

/**
 * @author rockyshen
 * @date 2024/11/14 22:42
 * 利用装饰器模式，将RecordParser对原来的buffer处理能力进行增强
 * 封装到Tcp、Handler，解决半包、粘包问题
 */
public class TcpBufferHandlerWrapper implements Handler<Buffer> {
    private final RecordParser recordParser;

    // 构造器
    public TcpBufferHandlerWrapper(Handler<Buffer> bufferHandler){
        recordParser = initRecordParser(bufferHandler);
    }

    @Override
    public void handle(Buffer buffer) {
        recordParser.handle(buffer);
    }

    private RecordParser initRecordParser(Handler<Buffer> bufferHandler) {
        RecordParser parser = RecordParser.newFixed(ProtocolConstant.MESSAGE_HEADER_LENGTH);

        parser.setOutput(new Handler<Buffer>() {
            int size = -1;   // body的byte数组的长度
            // 先构造一个空的resultBuffer
            Buffer resultBuffer = Buffer.buffer();

            @Override
            public void handle(Buffer buffer) {
                if(-1 == size){
                    size = buffer.getInt(13);  // index=13 正好记录的时body的长度
                    parser.fixedSizeMode(size);  // 读取头信息中的body长度，然后修改定长，去读body
                    resultBuffer.appendBuffer(buffer);  // 写入头信息
                }else {
                    resultBuffer.appendBuffer(buffer);  // 写入体信息
                    bufferHandler.handle(resultBuffer);   // 拼接为完整Buffer，执行处理

                    // 重置一轮
                    parser.fixedSizeMode(ProtocolConstant.MESSAGE_HEADER_LENGTH);
                    size = -1;
                    resultBuffer = Buffer.buffer();
                }
            }
        });
        return parser;
    }



}
