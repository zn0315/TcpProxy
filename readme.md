# tcp proxy
##问题的提出
有时候要测试程序在低速网络下的行为。tcptrace和nc都可以做ip proxy,但网络时延不支持或不好用。所以开发这个util.来模拟低速网络。
##Usage
TcpProxy &lt;src port&gt; &lt;dst ip&gt; &lt;dst port&gt; [-tracelevel level] [-delay milliseconds]

## this version
insert every delay for each package. buffer size is 1024.
## next version
support policy

1. delay every package
2. random insert delay
3. always delay but delay time length is random
4. random delay and random time length
