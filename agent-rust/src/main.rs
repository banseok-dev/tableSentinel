// tableSentienl agent rust project

async fn say_hello() -> String {
    "test async function".to_string()
}

#[tokio::main]
async fn main() {
    println!("tableSentinel Rust Agent");

    let result = say_hello().await;
    println!("Result: {}", result);
}