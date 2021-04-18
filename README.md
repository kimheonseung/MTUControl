### Controlling Maximum Transmission Unit (MTU) with Java Native Library
- Usage 
```java
/* 빌드 전, 생성자에 네트워크 인터페이스명 기입 필요 */
public MTUChanger() {
    this.ip = NetworkInterfaceUtils.getInstance().getIPv4HostAddressByInterfaceName("eth1");
    this.MTU_CHANGE_COMMAND = getMTUChangeCommand(OSHelper.getInstance().isWindows());
}


/* 576 (Windows), 220 (Linux) ~ 1500 사이에서 호출마다 10씩 증가하여 MTU값 조절 */
/* 1500인 경우 다시 최소값으로 조절됨 */
MTUChanger.getInstance().changeMTU();
```
