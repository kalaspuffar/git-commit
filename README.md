# git-commit

A simple example how to commit in pure java to a git repository

## Init a git repository

Initializing a git repository is quite simple. You just need to create a data structure like this.

```
.git/objects (dir)
.git/refs/heads (dir)
.git/HEAD (file)
```

The head file needs a reference to the branch your currently working on for instance to reference to the master branch you type

```
ref:refs/heads/master
```

## Creating a piece of content

To actually create a some content into the repository you need to create a new object.

The object has a header with type and size and then the actual content. Below you see an example with "Hello world" as content.

```
blob 11<null char>Hello world
```

After that you create a hash of that object and Deflate it (package it with zip).

The sha-1 hash in this case something like 95d09f2b10159347eece71399a7e2e907ea3df4f is used to save the file in the objects directory.

```
.git/objects/95/d09f2b10159347eece71399a7e2e907ea3df4f
```

As you see above the hash is split so you use the first 2 characters as a separating directory. We separate like this in order to avoid the upper limit of to many files in one directory. The zipped file data is saved to this file and we have stored the content in the object database of git.

## Setting up a file directory

Next up we need to define the file that this content is connected to. This part gets a bit technical and kinda hard to understand the reasoning around. It took me a while in the video to figure out this structure.

Files and directories are saved in tree objects and a tree object have an special structure.

As with content object we have a header followed by null character (\0) and then the content.
```
tree 78<null char><content>
```

The content in part has first file permissions flags (644 = read/write read read) then a space, followed by the name of the file or directory we want to connect to. Then we add a null character (\0) followed by 20 bytes of hash. So this is the raw hash value not the hex value of the hash. This tripped me up when I tried to implement it in java.
```
100644 test.txt<null char>Ð+GîÎq9~.~£ßO
```

This file is similarly saved with it's hash 6145aed96b0bfab5d9cb75846128b20c618c2198 to the object store.

## Adding a commit to your git repository

Last but not least we want to add an commit to our repository. This is pretty strait forward. We can reuse the code from creating the content blob and just change the type.

__Header definition__
```
commit 194<null char><content>
```

A commit is connected to a tree that is a set of files or folders that is stored as a snapshot for this commit. Then we specify the author of the code followed by a time-stamp with difference to GMT. For me +0100 is one hour after GMT which is my timezone. Committer is the person actually creating the commit, it will differ from the author when you pull data or merge. Then we add two new lines and a commit message and end the content with two more new lines. The newlines are a standard concept that we can recognize from HTTP or SMTP where header information and body is separated by two extra newlines. 
```
tree 6145aed96b0bfab5d9cb75846128b20c618c2198
author Daniel Persson<author@example.com> 1504690582 +0100
committer Daniel Persson<committer@example.com> 1504690582 +0100

My commit message


```
This file is similarly saved with it's hash ed81203769b2c4022321077f7eeebd6ca344dd81 to the object store.

## Creating the master branch

The last part of this example is to create the master branch. And if we remember from the first step where we initialized the repository we created a reference to refs/heads/master. But we never created that file? Well we could have a reference to a missing file until we actually commit our first commit.

Now that we have a commit we could create a new file and just add the commit for example ed81203769b2c4022321077f7eeebd6ca344dd81 to that file.

The refs/heads could have other files used for different branches and if you want to create a tag you add your commit hash to a file under refs/tags.

## Video

[Git commit in pure java](https://www.youtube.com/watch?v=KCzNwIx8XHg)

Hope this was helpful.

If you have any questions contact me on twitter @kalaspuffar
