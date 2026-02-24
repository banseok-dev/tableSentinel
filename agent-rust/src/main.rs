// tableSentienl agent rust project

use tokio;
use uuid;

pub struct Agentidentity{
    pub uuid: String
}

impl Agentidentity{
    fn new() -> Self{
        let uuid = uuid::Uuid::new_v4();
        let uuid = uuid.to_string();
        Self { uuid }
    }
    fn print_id(&self) {
        println!("it's your id {0}", self.uuid);
    }
}


#[tokio::main]
async fn main() {
    println!("tableSentinel Rust Agent");

    let result = say_hello().await;
    println!("Result: {}", result);
    
    let my_id = Agentidentity::new();
    my_id.print_id()
}

async fn say_hello() -> String {
    "test async function".to_string()
}