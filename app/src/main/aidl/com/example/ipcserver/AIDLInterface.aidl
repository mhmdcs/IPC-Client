// AIDLInterface.aidl
package com.example.ipcserver;

//Copy the same exact AIDL Interface we created in our server application to our client application, including the same package name.
//Important note!! : The directory where the file is located must be the same in both applications. When we create the file with the help of IDE, it will automatically be created under the directory src/main/aidl/com/example/ipcclient/
//and we need to update the directory to src/main/aidl/com/example/ipcserver/

// An Interface Description Language or Interface Definition Language (IDL), is a generic term for a language that lets a program or object written in one language communicate with another program written in an unknown language. IDLs describe an interface in a language-independent way, enabling communication between software components that do not share one language.

// AIDL is an Android Java-based IDL that supports local and remote procedure calls, it can be accessed from native applications by calling through Java Native Interface (JNI)

// Declare any non-default types here with import statements

interface AIDLInterface {

    // AIDL methods:

    // Request the process ID of this service (the server application).
    int getPid();

    // Get information about how many times the server application has received a client connection request during its life-cycle.
    int getConnectionCount();

    // The client can set some information about itself to the server such as its package name, PID, and send some arbitrary data. The server will then display this information on its GUI.
    void setDisplayedValue(String packageName, int pid, String data);
}

// Note that there are restrictions for the parameters and return types that AIDL methods take.
// Accepted types are all primitive types in Java (such as int, long, char boolean, etc), Arrays of primitive types, String, CharSequence, & Lists and Maps of the aforementioned types.
// If we want to use a class that we have defined, it needs to be parcelable (converting complex objects into byte streams) so that it is parsed to a level that the operating system can understand.