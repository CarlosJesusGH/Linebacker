/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.9
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.doubango.tinyWRAP;

public class OptionsSession extends SipSession {
    private long swigCPtr;

    protected OptionsSession(long cPtr, boolean cMemoryOwn) {
        super(tinyWRAPJNI.OptionsSession_SWIGUpcast(cPtr), cMemoryOwn);
        swigCPtr = cPtr;
    }

    protected static long getCPtr(OptionsSession obj) {
        return (obj == null) ? 0 : obj.swigCPtr;
    }

    protected void finalize() {
        delete();
    }

    public synchronized void delete() {
        if (swigCPtr != 0) {
            if (swigCMemOwn) {
                swigCMemOwn = false;
                tinyWRAPJNI.delete_OptionsSession(swigCPtr);
            }
            swigCPtr = 0;
        }
        super.delete();
    }

    public OptionsSession(SipStack pStack) {
        this(tinyWRAPJNI.new_OptionsSession(SipStack.getCPtr(pStack), pStack), true);
    }

    public boolean send(ActionConfig config) {
        return tinyWRAPJNI.OptionsSession_send__SWIG_0(swigCPtr, this, ActionConfig.getCPtr(config), config);
    }

    public boolean send() {
        return tinyWRAPJNI.OptionsSession_send__SWIG_1(swigCPtr, this);
    }

    public boolean accept(ActionConfig config) {
        return tinyWRAPJNI.OptionsSession_accept__SWIG_0(swigCPtr, this, ActionConfig.getCPtr(config), config);
    }

    public boolean accept() {
        return tinyWRAPJNI.OptionsSession_accept__SWIG_1(swigCPtr, this);
    }

    public boolean reject(ActionConfig config) {
        return tinyWRAPJNI.OptionsSession_reject__SWIG_0(swigCPtr, this, ActionConfig.getCPtr(config), config);
    }

    public boolean reject() {
        return tinyWRAPJNI.OptionsSession_reject__SWIG_1(swigCPtr, this);
    }

}
