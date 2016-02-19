package com.cmsys.linebacker.voip_webrtc;

import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.cmsys.linebacker.R;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.VideoCapturerAndroid;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoRendererGui;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.util.ArrayList;
import java.util.List;

import static com.cmsys.linebacker.util.LogUtils.makeLogTag;

public class WebRtcActivity extends AppCompatActivity {

    private static final String TAG = makeLogTag(WebRtcActivity.class);
    private static final String VIDEO_TRACK_ID = "video_track_id";
    private static final String AUDIO_TRACK_ID = "audio_track_id";
    private static final String LOCAL_MEDIA_STREAM_ID = "local_media_stream_id";
    //
    private VideoSource mVideoSource;
    private PeerConnectionFactory mPeerConnectionFactory;
    private AudioTrack mLocalAudioTrack;
    private VideoTrack mLocalVideoTrack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_rtc);

        // Setup WebRTC
        setupWebRTC();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mVideoSource.stop();
        Log.i(TAG, "Camera released");
    }

    private void setupWebRTC() {
        Log.i(TAG, "setupWebRTC");
        // First, we initiate the PeerConnectionFactory with
        // our application context and some options.
        // Refer to: https://tech.appear.in/2015/05/25/Introduction-to-WebRTC-on-Android/
        boolean isInitialized = PeerConnectionFactory.initializeAndroidGlobals(
                getApplicationContext(),    // context,
                true,   // initializeAudio,
                true,   // initializeVideo,
                true,   // videoCodecHwAcceleration,    //A boolean for enabling hardware acceleration.
                null);  // renderEGLContext);   // Can be provided to support HW video decoding to texture...

        if (isInitialized) {
            // Create your factory using the PeerConnectionFactory constructor.
            mPeerConnectionFactory = new PeerConnectionFactory();
            // Setup video source
            setupVideoSource(mPeerConnectionFactory);
            // Setup audio source
            setupAudioSource();
            // Setup video renderer
            setupVideoRenderer();
            // Create MediaStream
            createMediaStream();
            // Create Peer Connection
            createPeerConnection();
        }
    }

    private void setupVideoSource(PeerConnectionFactory peerConnectionFactory) {
        // Returns the front face device name
        String frontCameraName = VideoCapturerAndroid.getNameOfFrontFacingDevice();

        // Returns the back facing device name
        String backCameraName = VideoCapturerAndroid.getNameOfBackFacingDevice();

        // Creates a VideoCapturerAndroid instance for the device name
        VideoCapturerAndroid capturer = VideoCapturerAndroid.create(frontCameraName);
        VideoCapturerAndroid capturerOwn = VideoCapturerAndroid.create(frontCameraName);

        // Creates a video constraint (not sure what it is for)
        MediaConstraints videoConstraints = new MediaConstraints();

        // First we create a VideoSource
        mVideoSource = peerConnectionFactory.createVideoSource(capturer, videoConstraints);
        //mVideoSourceOwn = peerConnectionFactory.createVideoSource(capturerOwn, videoConstraints);

        // Once we have that, we can create our VideoTrack
        // Note that VIDEO_TRACK_ID can be any string that uniquely
        // identifies that video track in your application
        mLocalVideoTrack = peerConnectionFactory.createVideoTrack(VIDEO_TRACK_ID, mVideoSource);
    }

    private void setupAudioSource() {
        // Creates a video constraint (not sure what it is for)
        MediaConstraints audioConstraints = new MediaConstraints();

        // First we create an AudioSource
        AudioSource audioSource =
                mPeerConnectionFactory.createAudioSource(audioConstraints);

        // Once we have that, we can create our AudioTrack
        // Note that AUDIO_TRACK_ID can be any string that uniquely
        // identifies that audio track in your application
        mLocalAudioTrack = mPeerConnectionFactory.createAudioTrack(AUDIO_TRACK_ID, audioSource);
    }

    private void setupVideoRenderer() {
        // To create our VideoRenderer, we can use the
        // included VideoRendererGui for simplicity
        // First we need to set the GLSurfaceView that it should render to
        GLSurfaceView videoView = (GLSurfaceView) findViewById(R.id.glview_call);

        //*************
        // Add this to avoid error: “No config chosen”
        // http://stackoverflow.com/questions/14167319/android-opengl-demo-no-config-chosen
        videoView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        //*************

        // Then we set that view, and pass a Runnable
        // to run once the surface is ready
        VideoRendererGui.setView(videoView, null);

        // Now that VideoRendererGui is ready, we can get our VideoRenderer
        VideoRenderer.Callbacks remoteRenderer = null, localRenderer = null;

        try {
            remoteRenderer = VideoRendererGui.create(
                    0,      // x,
                    0,      // y,
                    100,    // videoView.getWidth(),   // width,
                    100,    // videoView.getHeight(),  // height,
                    // null,                                            // ScalingType (looks stretched out)
                    //VideoRendererGui.ScalingType.SCALE_FILL,          // ScalingType (looks very good, just a little stretched)
                    VideoRendererGui.ScalingType.SCALE_ASPECT_FILL,   // ScalingType (looks great)
                    //VideoRendererGui.ScalingType.SCALE_ASPECT_FIT,    // ScalingType (looks good but insert a black border)
                    true); // mirror);

            localRenderer = VideoRendererGui.create(
                    0,      // x,
                    0,      // y,
                    100,    // videoView.getWidth(),   // width,
                    100,    // videoView.getHeight(),  // height,
                    // null,                                            // ScalingType (looks stretched out)
                    //VideoRendererGui.ScalingType.SCALE_FILL,          // ScalingType (looks very good, just a little stretched)
                    VideoRendererGui.ScalingType.SCALE_ASPECT_FILL,   // ScalingType (looks great)
                    //VideoRendererGui.ScalingType.SCALE_ASPECT_FIT,    // ScalingType (looks good but insert a black border)
                    true); // mirror);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // And finally, with our VideoRenderer ready, we
        // can add our renderer to the VideoTrack.
        mLocalVideoTrack.addRenderer(new VideoRenderer(localRenderer));

        final VideoRenderer.Callbacks finalRemoteRenderer = remoteRenderer;
        final VideoRenderer.Callbacks finalLocalRenderer = localRenderer;
        ((Button) findViewById(R.id.bWebRtcCall)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLocalVideoTrack.removeRenderer(new VideoRenderer(finalRemoteRenderer));
                mLocalVideoTrack.removeRenderer(new VideoRenderer(finalLocalRenderer));
                //
                mLocalVideoTrack.addRenderer(new VideoRenderer(finalRemoteRenderer));
                mLocalVideoTrack.addRenderer(new VideoRenderer(finalLocalRenderer));
                //
                VideoRendererGui.update(finalRemoteRenderer, 0, 0, 100, 100, VideoRendererGui.ScalingType.SCALE_ASPECT_FILL, false);
                VideoRendererGui.update(finalLocalRenderer, 72, 72, 25, 25, VideoRendererGui.ScalingType.SCALE_ASPECT_FIT, true);
            }
        });

        ((Button) findViewById(R.id.bWebRtcEnd)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLocalVideoTrack.removeRenderer(new VideoRenderer(finalRemoteRenderer));
                mLocalVideoTrack.removeRenderer(new VideoRenderer(finalLocalRenderer));
                //
                mLocalVideoTrack.addRenderer(new VideoRenderer(finalLocalRenderer));
                //
                VideoRendererGui.update(finalLocalRenderer, 0, 0, 100, 100, VideoRendererGui.ScalingType.SCALE_ASPECT_FILL, true);
            }
        });

    }

    private void createMediaStream() {
        // We start out with an empty MediaStream object,
        // created with help from our PeerConnectionFactory
        // Note that LOCAL_MEDIA_STREAM_ID can be any string
        MediaStream mediaStream = mPeerConnectionFactory.createLocalMediaStream(LOCAL_MEDIA_STREAM_ID);

        // Now we can add our tracks.
        mediaStream.addTrack(mLocalVideoTrack);
        mediaStream.addTrack(mLocalAudioTrack);
    }

    private void createPeerConnection() {
        PCObserver observer = new PCObserver();
        MediaConstraints constraints = new MediaConstraints();
        PeerConnection.IceServer iceServer = new PeerConnection.IceServer("localhost");
        List<PeerConnection.IceServer> iceServers = new ArrayList<>();
        iceServers.add(iceServer);

        PeerConnection peerConnection = mPeerConnectionFactory.createPeerConnection(
                iceServers,
                constraints,
                observer);
    }


    // Implementation detail: observe ICE & stream changes and react accordingly.
    private class PCObserver implements PeerConnection.Observer {
        @Override
        public void onIceCandidate(final IceCandidate candidate) {
            /*runOnUiThread(new Runnable() {
                public void run() {
                    JSONObject json = new JSONObject();
                    jsonPut(json, "type", "candidate");
                    jsonPut(json, "label", candidate.sdpMLineIndex);
                    jsonPut(json, "id", candidate.sdpMid);
                    jsonPut(json, "candidate", candidate.sdp);
                    sendMessage(json);
                }
            });*/
        }

        //@Override
        public void onError() {
            runOnUiThread(new Runnable() {
                public void run() {
                    throw new RuntimeException("PeerConnection error!");
                }
            });
        }

        @Override
        public void onSignalingChange(
                PeerConnection.SignalingState newState) {
        }

        @Override
        public void onIceConnectionChange(
                PeerConnection.IceConnectionState newState) {
        }

        @Override
        public void onIceGatheringChange(
                PeerConnection.IceGatheringState newState) {
        }

        @Override
        public void onAddStream(final MediaStream stream) {
            /*runOnUiThread(new Runnable() {
                public void run() {
                    abortUnless(stream.audioTracks.size() <= 1 &&
                                    stream.videoTracks.size() <= 1,
                            "Weird-looking stream: " + stream);
                    if (stream.videoTracks.size() == 1) {
                        stream.videoTracks.get(0).addRenderer(
                                new VideoRenderer(remoteRender));
                    }
                }
            });*/
        }

        @Override
        public void onRemoveStream(final MediaStream stream) {
            runOnUiThread(new Runnable() {
                public void run() {
                    stream.videoTracks.get(0).dispose();
                }
            });
        }

        @Override
        public void onDataChannel(final DataChannel dc) {
            runOnUiThread(new Runnable() {
                public void run() {
                    throw new RuntimeException(
                            "AppRTC doesn't use data channels, but got: " + dc.label() +
                                    " anyway!");
                }
            });
        }

        @Override
        public void onRenegotiationNeeded() {
            // No need to do anything; AppRTC follows a pre-agreed-upon
            // signaling/negotiation protocol.
        }
    }
}
