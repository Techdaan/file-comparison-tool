# file-comparison-tool
A simple tool to check files that might have been copy-pasted. This is achieved by generating a reverse-lookup table using the file hashes in the source directory, then matching those with the hashes of the files in the target directory.  

### Usage
Simply download a release and execute the jar file.  
  
The most basic example: `java -jar file-comparison-tool.jar --source "./path/to/source" --target "./path/to/target"`  
  
When necessary, you can add a `--algorithm` parameter to define which algorithm should be used to generate file hashes. By default `sha-256` is used. The digest is obtained through Java's `MessageDigest`. 
  
You can skip the confirmation prompt by adding the `--confirm` flag.