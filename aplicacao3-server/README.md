# Aplicação 3 - Serviços Web (SERVER)
Sistemas Distribuídos [CSS30-S71]

## Para rodar a aplicação
```$ mvn clean compile && mvn exec:java```

## Funcionamento (Cliente JS)
- Para regitrar interesse em um recurso:
```JavaScript
const eventSource = EventSource('/resource1'); // ou '/resource2'
```

- Para desregistrar interesse ou liberar recurso
```JavaScript
eventSource.close();
```

## Mensagens enviadas pelo servidor (por Server-Sent Events)
- Cliente adicionado à fila: <br/>
`[RECURSO1] waiting` <br/>
ou <br/>
`[RECURSO2] waiting` <br/><br/>
- Recurso disponível: <br/>
`[RECURSO1] available` <br/>
ou <br/>
`[RECURSO2] available` <br/><br/>
- Tempo limite de posse de recurso atingido: <br/>
`[RECURSO1] timeout` <br/>
ou <br/>
`[RECURSO2] timeout`