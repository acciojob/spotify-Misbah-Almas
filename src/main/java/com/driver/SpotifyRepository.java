package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<Playlist, User> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {
        User user = new User(name, mobile);
        users.add(user);
        return user;
    }

    public Artist createArtist(String name) {
        Artist artist = new Artist(name);
        artists.add(artist);
        return artist;
    }

    public Album createAlbum(String title, String artistName) {
        Artist artist = new Artist(artistName);
        for(Artist itr : artists){
            if(itr.getName().equals(artistName)){
                artist = itr;
                break;
            }
        }
        if(!artists.contains(artist)){
            artists.add(artist);
        }
        Album album = new Album(title);
        albums.add(album);

        List<Album> albumList = artistAlbumMap.getOrDefault(artist, new ArrayList<Album>());
        albumList.add(album);
        artistAlbumMap.put(artist, albumList);

        return album;
    }

    public Song createSong(String title, String albumName, int length) throws Exception{
        boolean flag = false;
        Album album = null;
        for(Album itr : albums){
            if(itr.getTitle().equals(albumName)){
                flag = true;
                album = itr;
                break;
            }
        }
        if(!flag){
            throw new Exception("Album does not exist");
        }

        Song song = new Song(title, length);
        songs.add(song);

        if(flag){
            List<Song> songList = albumSongMap.getOrDefault(album, new ArrayList<Song>());
            songList.add(song);
            albumSongMap.put(album, songList);
        }
        return song;
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        List<Song> songList = new ArrayList<Song>();
        boolean flag = false;
        User user = null;
        for(User itr : users){
            if(itr.getMobile().equals(mobile)){
                flag = true;
                user = itr;
                break;
            }
        }

        if(!flag){
            throw new Exception("User does not exist");
        }

        Playlist playlist = new Playlist(title);
        playlists.add(playlist);

        for(Song itr : songs){
            if(itr.getLength()==length){
                songList.add(itr);
            }
        }
        playlistSongMap.put(playlist, songList);
        if(flag){
            List<User> userList = playlistListenerMap.getOrDefault(playlist, new ArrayList<User>());
            userList.add(user);
            playlistListenerMap.put(playlist, userList);
            creatorPlaylistMap.put(playlist, user);
            List<Playlist> playlistList = userPlaylistMap.getOrDefault(user, new ArrayList<Playlist>());
            playlistList.add(playlist);
            userPlaylistMap.put(user, playlistList);
        }
        return playlist;
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        List<Song> songList = new ArrayList<Song>();
        boolean flag = false;
        User user = null;
        for(User itr : users){
            if(itr.getMobile().equals(mobile)){
                flag = true;
                user = itr;
                break;
            }
        }

        if(!flag){
            throw new Exception("User does not exist");
        }

        Playlist playlist = new Playlist(title);
        playlists.add(playlist);

        for(String songTitle : songTitles){
            for(Song itr : songs){
                if(itr.getTitle().equals(songTitle)){
                    songList.add(itr);
                }
            }
        }
        playlistSongMap.put(playlist, songList);
        if(flag){
            List<User> userList = playlistListenerMap.getOrDefault(playlist, new ArrayList<User>());
            userList.add(user);
            playlistListenerMap.put(playlist, userList);
            creatorPlaylistMap.put(playlist, user);
            List<Playlist> playlistList = userPlaylistMap.getOrDefault(user, new ArrayList<Playlist>());
            playlistList.add(playlist);
            userPlaylistMap.put(user, playlistList);
        }
        return playlist;
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        User user = null;
        Playlist playlist = null;
        boolean flagUser = false;
        for(User itr : users){
            if(itr.getMobile().equals(mobile)){
                flagUser = true;
                user = itr;
                break;
            }
        }

        if(!flagUser){
            throw new Exception("User does not exist");
        }

        boolean flagPlaylist = false;
        for(Playlist itr : playlists){
            if(itr.getTitle().equals(playlistTitle)){
                flagPlaylist = true;
                playlist = itr;
                break;
            }
        }

        if(!flagPlaylist){
            throw new Exception("Playlist does not exist");
        }

        if(flagUser && flagPlaylist){
            if(!playlistListenerMap.get(playlist).contains(user)){
                playlistListenerMap.get(playlist).add(user);
                List<Playlist> playlistList = userPlaylistMap.getOrDefault(user, new ArrayList<Playlist>());
                playlistList.add(playlist);
                userPlaylistMap.put(user, playlistList);
            }
        }
        return playlist;
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        User user = new User();
        Song song = new Song();
        boolean flagUser = false;
        for(User itr : users){
            if(itr.getMobile().equals(mobile)){
                flagUser = true;
                user = itr;
                break;
            }
        }

        if(!flagUser){
            throw new Exception("User does not exist");
        }

        boolean flagSong = false;
        for(Song itr : songs){
            if(itr.getTitle().equals(songTitle)){
                flagSong = true;
                song = itr;
                break;
            }
        }

        if(!flagSong){
            throw new Exception("Song does not exist");
        }

        if(flagUser && flagSong){
            List<User> userList = songLikeMap.getOrDefault(song, new ArrayList<User>());
            if(!userList.contains(user)){
                userList.add(user);
                song.setLikes(song.getLikes()+1);

                Album album = new Album();
                for(Album albumItr : albumSongMap.keySet()){
                    if(albumSongMap.get(albumItr).contains(song)){
                        album = albumItr;
                        break;
                    }
                }

                Artist artist = new Artist();
                for(Artist artistItr : artistAlbumMap.keySet()){
                    if(artistAlbumMap.get(artistItr).contains(album)){
                        artist = artistItr;
                        break;
                    }
                }
                artist.setLikes(artist.getLikes()+1);
            }
        }

        return song;
    }

    public String mostPopularArtist() {
        int max = Integer.MIN_VALUE;
        String name = "";
        for(Artist artist : artists){
            if(artist.getLikes() > max){
                max = artist.getLikes();
                name = artist.getName();
            }
        }
        return name;
    }

    public String mostPopularSong() {
        int max = Integer.MIN_VALUE;
        String title = "";
        for(Song song : songs){
            if(song.getLikes() > max){
                max = song.getLikes();
                title = song.getTitle();
            }
        }
        return title;
    }
}
