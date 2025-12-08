# TEAM 'TEN'
## TEAM Introduction
Hello, we are team 'TEN'. Our team consists of the following members:  

| Name | Email | Github Address |  
|------|-------|----------------|  
| JIAKANG CHEN | chenjiakang73@gmail.com | [https://github.com/chenjiakang1](https://github.com/chenjiakang1) |  
| ZIXIAN LI | xli76218628@gmail.com | [https://github.com/ninemorning](https://github.com/ninemorning) |  
| JIAWEI CHENG | 2023089716@hanyang.ac.kr | [https://github.com/weiwei20040619-web](https://github.com/weiwei20040619-web) |  
| ZHIJUN SHUAI | zhijunshuai8@gmail.com | [https://github.com/SHUAIZHIJUN](https://github.com/SHUAIZHIJUN) |  
| LEFAN XU | xulefan0731@gmail.com | [https://github.com/xlf06](https://github.com/xlf06) |  
| ZHAONING LIU | liuzic0329@gmail.com | [https://github.com/962464thdl](https://github.com/962464thdl) |  
| HONGBIN MIAO | mikey813real@gmail.com | [https://github.com/mikey813](https://github.com/mikey813) |  

---

## Team Requirements

Our team is responsible for:  

- Designing and implementing the **Two-player Mode** system.  
- Modifying `InputManager` to handle inputs from both players.  
- Managing two `Ship` objects in `GameScreen` and tracking each player’s score and lives in `GameState`.  
- Updating the HUD to display information for both players.  
- Coordinating with Team 9 to provide a distinct ship design for the second player.  
- Ensuring compatibility with other gameplay systems (Level Design, Items, Currency).  

---

## Detailed Requirements

### Functional Requirements  
1. **Input Handling**  
   - Extend `InputManager` to process controls from Player 1 and Player 2 without conflicts.  

2. **Game State Management**  
   - Add support in `GameScreen` to manage two `Ship` objects simultaneously.  
   - Track each player’s score, lives, and status separately in `GameState`.  

3. **HUD and Display**  
   - Modify the HUD (in collaboration with Team 8) to show both players’ information clearly and simultaneously.  

4. **Ship Design**  
   - Work with Team 9 to introduce a unique ship design for Player 2.  

5. **System Integration**  
   - Ensure smooth integration with all core gameplay systems, including level progression, items, and currency mechanics.  

### Non-functional Requirements  
- Maintain stable performance with two active players.  
- Ensure code changes are modular, maintainable, and well-documented.  
- Provide thorough testing to verify multi-player interactions.  
