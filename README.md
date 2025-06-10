# Servidor FTP Simples

Este é um sistema FTP simples implementado em Java, consistindo em um servidor e um cliente. O sistema permite transferência de arquivos, listagem de diretório e gerenciamento de arquivos remotos.

## Requisitos

- Java JDK 8 ou superior
- Sistema operacional: Windows, Linux ou macOS

## Como Compilar

1. Abra o terminal na pasta do projeto
2. Execute o comando para compilar os arquivos:
```bash
javac FTPServer.java FTPClient.java
```

## Como Executar

### Iniciando o Servidor

1. Abra um terminal na pasta do projeto
2. Execute o comando:
```bash
java FTPServer
```
3. O servidor iniciará na porta 9090
4. Uma mensagem "Servidor FTP iniciado na porta 9090" será exibida
5. O servidor criará automaticamente um diretório `ftp_storage` para armazenar os arquivos

### Iniciando o Cliente

1. Abra outro terminal na pasta do projeto
2. Execute o comando:
```bash
java FTPClient
```
3. O cliente se conectará automaticamente ao servidor local na porta 9090
4. Um menu com as opções disponíveis será exibido

## Funcionalidades

### 1. Listar Arquivos
- Opção 1 no menu
- Lista todos os arquivos disponíveis no servidor
- Se não houver arquivos, exibe "Nenhum arquivo encontrado"

### 2. Enviar Arquivo
- Opção 2 no menu
- Permite enviar um arquivo para o servidor
- Digite o caminho completo do arquivo quando solicitado
- O arquivo será armazenado no diretório `ftp_storage` do servidor

### 3. Baixar Arquivo
- Opção 3 no menu
- Permite baixar um arquivo do servidor
- Digite o nome do arquivo que deseja baixar
- Digite o diretório onde deseja salvar o arquivo
- Mostra uma barra de progresso durante o download
- Verifica a integridade do download

### 4. Excluir Arquivo
- Opção 4 no menu
- Permite excluir um arquivo do servidor
- Digite o nome do arquivo que deseja excluir
- Confirmação será solicitada (S/N)
- Exibe mensagem de sucesso ou erro após a tentativa de exclusão

### 5. Sair
- Opção 5 no menu
- Desconecta do servidor
- Encerra o programa cliente

## Estrutura de Arquivos

```
.
├── FTPServer.java    # Código do servidor
├── FTPClient.java    # Código do cliente
├── README.md         # Este arquivo
└── ftp_storage/      # Diretório onde os arquivos são armazenados (criado automaticamente)
```

## Observações Importantes

1. O servidor deve estar em execução antes de iniciar o cliente
2. Os arquivos enviados são armazenados na pasta `ftp_storage`
3. O servidor aceita múltiplas conexões simultâneas
4. Para enviar um arquivo, você precisa fornecer o caminho completo do arquivo
5. Para baixar um arquivo, você precisa fornecer apenas o nome do arquivo
6. O sistema verifica a integridade dos downloads
7. Arquivos com download incompleto são automaticamente removidos

## Tratamento de Erros

O sistema inclui tratamento para os seguintes casos:
- Arquivo não encontrado
- Erro de conexão
- Download incompleto
- Diretório inexistente (cria automaticamente)
- Confirmação antes de excluir arquivos

## Resolução de Problemas

1. **Erro "Address already in use"**
   - O servidor já está em execução ou a porta 9090 está em uso
   - Encerre o processo anterior ou altere a porta nos arquivos fonte

2. **Cliente não conecta**
   - Verifique se o servidor está em execução
   - Confirme se a porta 9090 está livre
   - Verifique se não há firewall bloqueando a conexão

3. **Erro ao enviar/baixar arquivos**
   - Verifique as permissões do diretório
   - Confirme se há espaço em disco suficiente
   - Verifique se o caminho do arquivo está correto

