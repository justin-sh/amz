db.products.updateMany ({isOverdueTransferred:{$exists:false}},{$set:{isOverdueTransferred:false}})
db.product_summary.updateMany ({countOfOverdue:{$exists:false}},{$set:{countOfOverdue:0}})
