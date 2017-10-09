package com.flurgle.camerakit;

import android.util.Log;

import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by localuser on 2017/3/17.
 */

public class Util {
    public static boolean mergeMediaFiles(boolean isAudio, ArrayList<String> sourceFiles, String targetFile) {
        try {
            String mediaKey = isAudio ? "soun" : "vide";
            List<Movie> listMovies = new ArrayList<>();
            for (String filename : sourceFiles) {
                try {
                    Movie build = MovieCreator.build(filename);
                    listMovies.add(build);
                }catch (Exception e){
                    Log.e("Util_mergeMediaFiles",e.getCause().getMessage());
                }
            }
            // get the video and audio track.
            List<Track> videoTracks = new LinkedList<>();
            List<Track> audioTracks = new LinkedList<>();
            for (Movie movie : listMovies) {
                for (Track track : movie.getTracks()) {

                    if(track.getHandler().equals("soun")){
                        audioTracks.add(track);
                    }

                    if(track.getHandler().equals("vide")){
                        videoTracks.add(track);
                    }

                }
            }
            //merge together
            Movie outputMovie = new Movie();
            if (!videoTracks.isEmpty()) {
                outputMovie.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
            }

            if (!audioTracks.isEmpty()) {
                outputMovie.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
            }
            Container container = new DefaultMp4Builder().build(outputMovie);
            RandomAccessFile rwFile = new RandomAccessFile(String.format(targetFile), "rw");
            FileChannel fileChannel = rwFile.getChannel();
            container.writeContainer(fileChannel);
            fileChannel.close();
            rwFile.close();
            return true;
        }
        catch (IOException e) {
            Log.e("test", "Error merging media files. exception: "+e.getMessage());
            return false;
        }
    }
}
