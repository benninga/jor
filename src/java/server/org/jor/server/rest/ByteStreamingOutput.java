package org.jor.server.rest;

import java.io.IOException;
import java.io.OutputStream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

public class ByteStreamingOutput implements StreamingOutput
{
    private byte[] output;
    
    public ByteStreamingOutput(byte[] output)
    {
        this.output = output;
    }

    @Override
    public void write(OutputStream outputStream) throws IOException, WebApplicationException
    {
        outputStream.write(output);
    }
}
